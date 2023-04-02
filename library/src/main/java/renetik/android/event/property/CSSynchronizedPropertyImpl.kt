package renetik.android.event.property

import renetik.android.event.common.CSHasDestruct
import renetik.android.event.registration.registerOnMain

class CSSynchronizedPropertyImpl<T>(
    parent: CSHasDestruct? = null,
    value: T, onChange: ((value: T) -> Unit)? = null)
    : CSPropertyBase<T>(parent, onChange), CSSynchronizedProperty<T> {

    @get:Synchronized
    private var field: T = value

    override fun value(newValue: T, fire: Boolean): Unit = synchronized(this) {
        if (field == newValue) return
        field = newValue
        registerOnMain { onValueChanged(newValue) }
    }

    override var value: T
        get() = this.field
        set(value) = value(value)
}