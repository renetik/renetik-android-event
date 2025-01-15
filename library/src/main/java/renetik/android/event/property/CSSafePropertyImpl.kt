package renetik.android.event.property

import renetik.android.event.common.CSHasDestruct
import renetik.android.event.util.CSLater.onMain
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KProperty

class CSSafePropertyImpl<T>(
    parent: CSHasDestruct,
    value: T, onChangeUnsafe: ((value: T) -> Unit)? = null
) : CSPropertyBase<T>(parent, onChangeUnsafe), CSSafeProperty<T> {

    private val field = AtomicReference(value)

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }

    fun getAndSet(newValue: T): T {
        val previous = field.getAndSet(newValue)
        if (previous != newValue) onValueChanged(newValue)
        return previous
    }

    fun compareAndSet(value: T, newValue: T): Boolean {
        val isSet = field.compareAndSet(value, newValue)
        if (isSet) onValueChanged(newValue)
        return isSet
    }

    override fun value(newValue: T, fire: Boolean) {
        if (field.getAndSet(newValue) != newValue)
            onValueChanged(newValue, fire)
    }

    @Volatile
    override var isChanged = false

    override fun fireChange() = value.let {
        onChange?.invoke(it)
        onMain { eventChange.fire(it) }
    }

    override var value: T
        get() = this.field.get()
        set(value) = value(value)

    companion object {
        fun <T> CSHasDestruct.safeProperty(
            value: T, onChangeUnsafe: ((value: T) -> Unit)? = null
        ) = CSSafePropertyImpl(this, value, onChangeUnsafe)
    }
}

class CSAtomicProperty<T>(
    parent: CSHasDestruct? = null,
    value: T, onChange: ((value: T) -> Unit)? = null
) : CSPropertyBase<T>(parent, onChange) {

    companion object {
        fun <T> CSHasDestruct.nullAtomicProperty(
            value: T? = null, onChange: ((value: T?) -> Unit)? = null
        ) = CSAtomicProperty(this, value, onChange)

        fun <T> CSHasDestruct.atomicProperty(
            value: T, onChange: ((value: T) -> Unit)? = null
        ) = CSAtomicProperty(this, value, onChange)
    }


    private val field = AtomicReference(value)

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }

    fun getAndSet(newValue: T): T {
        val previous = field.getAndSet(newValue)
        if (previous != newValue) onValueChanged(newValue)
        return previous
    }

    fun compareAndSet(value: T, newValue: T): Boolean {
        val isSet = field.compareAndSet(value, newValue)
        if (isSet) onValueChanged(newValue)
        return isSet
    }

    override fun value(newValue: T, fire: Boolean) {
        if (field.getAndSet(newValue) != newValue)
            onValueChanged(newValue, fire)
    }

    @Volatile
    override var isChanged = false

    override fun fireChange() = value.let {
        onChange?.invoke(it)
        eventChange.fire(it)
    }

    override var value: T
        get() = this.field.get()
        set(value) = value(value)
}