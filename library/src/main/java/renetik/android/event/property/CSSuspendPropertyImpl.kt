package renetik.android.event.property

class CSSuspendPropertyImpl<T>(
    override var value: T,
    onApply: suspend ((value: T) -> Unit) = { }
) : CSSuspendPropertyBase<T>(onApply) {

    override suspend fun value(newValue: T, fire: Boolean) {
        if (this.value == newValue) return
        this.value = newValue
        onValueChanged(newValue, fire)
    }
}


