package renetik.android.event.property

class CSSynchronizedPropertyImpl<T>(
    value: T, onChange: ((value: T) -> Unit)? = null)
    : CSPropertyBase<T>(onChange), CSSynchronizedProperty<T> {

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