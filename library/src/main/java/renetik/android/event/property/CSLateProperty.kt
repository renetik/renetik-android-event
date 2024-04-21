package renetik.android.event.property

class CSLateProperty<T>(
    onApply: ((value: T) -> Unit)? = null
) : CSPropertyBase<T>(onApply) {

    var lateValue: T? = null

    override fun value(newValue: T, fire: Boolean) {
        if (lateValue == newValue) return
        lateValue = newValue
        onValueChanged(newValue, fire)
    }

    override var value: T
        get() = lateValue!!
        set(value) = value(value)

    override fun toString() = super.toString() + "$lateValue"
}