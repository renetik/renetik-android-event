@file:Suppress("NOTHING_TO_INLINE")
@file:OptIn(ExperimentalAtomicApi::class)

package renetik.android.event.registration

import kotlinx.coroutines.suspendCancellableCoroutine
import renetik.android.core.lang.ArgFun
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.common.CSHasDestruct
import renetik.android.event.common.CSHasRegistrationsHasDestruct
import renetik.android.event.common.CSModel
import renetik.android.event.common.onMain
import renetik.android.event.property.CSSafeHasChangeValue
import renetik.android.event.property.CSSafeHasChangeValueBase
import kotlin.Result.Companion.success
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.reflect.KProperty

// TODO?: eventUnsafeChange is never needed here it was added just because
//  CSSafeHasChangeValue requires it now
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
        override fun onUnsafeChange(function: (T) -> Unit) = eventUnsafeChange.listen(function)

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

inline fun <T> CSSafeHasChangeValue<T?>.isNull(): CSSafeHasChangeValue<Boolean> = isSetTo(null)

inline fun <T> CSSafeHasChangeValue<T?>.isNotNull(): CSSafeHasChangeValue<Boolean> = !isSetTo(null)

inline infix fun <T> CSSafeHasChangeValue<T>.isSetTo(value: T): CSSafeHasChangeValue<Boolean> =
    delegateValue(fromValue = { it == value })

suspend fun <T> CSSafeHasChangeValue<T>.waitFor(condition: (T) -> Boolean) {
    if (!condition(value)) suspendCancellableCoroutine { coroutine ->
        val registration = AtomicReference<CSRegistration?>(null)
        fun resume() = registration.exchange(null)?.apply {
            cancel()
            coroutine.resumeWith(success(Unit))
        }
        registration.store(onUnsafeChange { if (condition(value)) resume() })
        if (condition(value)) resume()
        coroutine.invokeOnCancellation { registration.exchange(null)?.cancel() }
    }
}

@JvmName("CSSafeHasChangeValueBooleanNot")
operator fun CSSafeHasChangeValue<Boolean>.not(): CSSafeHasChangeValue<Boolean> {
    val source = this
    return object : CSSafeHasChangeValueBase<Boolean>(initialValue = !source.value) {
        init {
            this + source.onUnsafeChange { value(!it) }
        }
    }
}

@JvmName("safeStateDelegate")
fun <Argument, Return> CSSafeHasChangeValue<Argument>.stateDelegate(
    parent: CSHasRegistrations? = null,
    unsafeFrom: (Argument) -> Return,
    onChange: ArgFun<Return>? = null
): CSSafeHasChangeValue<Return> = let { source ->
    object : CSSafeHasChangeValueBase<Return>(parent, unsafeFrom(source.value), onChange) {
        init {
            this + source.onUnsafeChange { value(unsafeFrom(it)) }
        }
    }
}

inline fun <T> CSSafeHasChangeValue<T>.unsafeAction(
    noinline function: (T) -> Unit): CSRegistration {
    onValue(function)
    return onUnsafeChange(function)
}

@JvmName("safeHasChangeValueIdentity")
fun <T> CSSafeHasChangeValue<T>.safeStateDelegate(
    parent: CSHasRegistrations? = null, onChange: ArgFun<T>? = null,
): CSSafeHasChangeValue<T> = stateDelegate(parent, unsafeFrom = { it }, onChange)

fun <Argument, Return> CSSafeHasChangeValue<Argument>.safeStateDelegate(
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