@file:OptIn(ExperimentalAtomicApi::class)

package renetik.android.event.property

import renetik.android.core.lang.ArgFun
import renetik.android.core.lang.value.CSSafeValue
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.common.CSHasDestruct
import renetik.android.event.common.CSHasRegistrationsHasDestruct
import renetik.android.event.common.CSModel
import renetik.android.event.common.onMain
import renetik.android.event.registration.CSHasChangeValue
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.plus
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.reflect.KProperty

interface CSSafeHasChangeValue<T> : CSSafeValue<T>, CSHasChangeValue<T> {

    fun onUnsafeChange(function: (T) -> Unit): CSRegistration

    companion object {
        fun <T> CSHasDestruct.safe(property: CSHasChangeValue<T>)
                : CSSafeHasChangeValue<T> = property.safe(this)

        fun <T> CSHasChangeValue<T>.safe(
            parent: CSHasDestruct? = null,
            onChange: ArgFun<T>? = null
        ): CSSafeHasChangeValue<T> = let { property ->
            object : CSModel(parent), CSSafeHasChangeValue<T> {
                private val _value = AtomicReference(property.value)
                val eventChange = event<T>()
                val eventUnsafeChange = event<T>()

                override var value: T
                    get() = _value.load()
                    set(value) = _value.store(value)

                override fun getValue(thisRef: Any?, property: KProperty<*>): T = value
                override fun onChange(function: (T) -> Unit) = eventChange.listen(function)
                override fun onUnsafeChange(function: (T) -> Unit) =
                    eventUnsafeChange.listen(function)

                init {
                    this + property.onChange { newValue ->
                        if (newValue != _value.exchange(newValue)) {
                            eventUnsafeChange.fire(newValue)
                            onMain {
                                onChange?.invoke(newValue)
                                eventChange.fire(newValue)
                            }
                        }
                    }
                }
            }
        }

        fun <T> CSHasRegistrations.safeValue(property: CSHasChangeValue<T>)
                : CSSafeValue<T> = property.safeValue(this)

        fun <T> CSHasChangeValue<T>.safeValue(
            parent: CSHasRegistrations): CSSafeValue<T> = let { property ->
            object : CSSafeValue<T> {
                @Volatile
                override var value: T = property.value
                override fun getValue(thisRef: Any?, property: KProperty<*>): T = value

                init {
                    parent + property.onChange { value = it }
                }
            }
        }

    }
}

internal open class CSSafeHasChangeValueBase<T>(
    parent: CSHasRegistrations? = null,
    initialValue: T,
    private val onSafeChange: ArgFun<T>? = null
) : CSModel(parent?.registrations), CSSafeHasChangeValue<T> {
    private val _value = AtomicReference(initialValue)
    private val eventUnsafeChange = event<T>()
    private val eventChange by lazy { event<T>() }

    override var value: T
        get() = _value.load()
        set(value) = _value.store(value)

    override fun onChange(function: (T) -> Unit) =
        eventChange.listen(function)

    override fun onUnsafeChange(function: (T) -> Unit) =
        eventUnsafeChange.listen(function)

    fun value(newValue: T, force: Boolean = false) {
        if (setValue(newValue) || force) onValueChanged(newValue)
    }

    @Synchronized
    protected fun setValueSilently(newValue: T) {
        value = newValue
    }

    @Synchronized
    private fun setValue(newValue: T): Boolean {
        if (value == newValue) return false
        value = newValue
        return true
    }

    protected open fun onValueChanged(newValue: T) {
        eventUnsafeChange.fire(newValue)
        onMain {
            if (registrations.isActive) {
                onSafeChange?.invoke(newValue)
                eventChange.fire(newValue)
            }
        }
    }
}

fun <Argument, Return> CSSafeHasChangeValue<Argument>.hasUnsafeChangeValue(
    parent: CSHasRegistrationsHasDestruct,
    from: (Argument) -> Return
): CSSafeHasChangeValue<Return> = let { source ->
    object : CSSafeHasChangeValueBase<Return>(parent, from(source.value)) {
        init {
            parent + source.onUnsafeChange {
                value(from(it))
            }
        }
    }
}
