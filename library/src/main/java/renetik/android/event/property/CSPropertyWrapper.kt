package renetik.android.event.property

abstract class CSPropertyWrapper<T> :
    CSProperty<T> {
    protected abstract val property: CSProperty<T>
    override fun value(newValue: T, fire: Boolean) = property.value(newValue, fire)
    override fun onChange(function: (T) -> Unit) = property.onChange(function)
    override var value: T
        get() = property.value
        set(value) = property.value(value)

    override fun fireChange() = property.fireChange()
}