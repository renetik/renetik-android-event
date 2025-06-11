package renetik.android.event.property

import renetik.android.core.lang.variable.CSSafeVariable
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KProperty

class CSSafeVariableImpl<T>(value: T) : CSSafeVariable<T> {

    private val field = AtomicReference(value)

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }

    override fun getAndSet(newValue: T): T = field.getAndSet(newValue)

    override fun compareAndSet(value: T, newValue: T): Boolean =
        field.compareAndSet(value, newValue)

    override var value: T
        get() = this.field.get()
        set(value) = this.field.set(value)

    companion object {
        fun <T> safeVar(value: T) = CSSafeVariableImpl(value)
    }
}