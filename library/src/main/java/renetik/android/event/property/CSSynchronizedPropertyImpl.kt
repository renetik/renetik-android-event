package renetik.android.event.property

import renetik.android.event.common.CSHasDestroy
import renetik.android.event.registration.onMain

class CSSynchronizedPropertyImpl<T>(
    parent: CSHasDestroy? = null,
    value: T, onChange: ((value: T) -> Unit)? = null)
    : CSPropertyBase<T>(parent, onChange), CSSynchronizedProperty<T> {

    @get:Synchronized
    private var _value: T = value

    override fun value(newValue: T, fire: Boolean): Unit = synchronized(this) {
        if (_value == newValue) return
        _value = newValue
        onMain { onValueChanged(newValue) }
    }

    override var value: T
        get() = _value
        set(value) = value(value)
}