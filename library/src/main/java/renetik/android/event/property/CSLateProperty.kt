package renetik.android.event.property

class CSLateProperty<T>(
    onApply: ((value: T) -> Unit)? = null) :
    CSPropertyBase<T>(onApply) {

    private var _value: T? = null

    override fun value(newValue: T, fire: Boolean) {
        if (_value == newValue) return
        _value = newValue
        onValueChanged(newValue, fire)
    }

    override var value: T
        get() = _value!!
        set(value) = value(value)
}