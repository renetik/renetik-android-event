package renetik.android.event.property

import renetik.android.event.common.CSHasDestruct
import renetik.android.event.common.CSModel

abstract class CSPropertyWrapper<T>(
    parent: CSHasDestruct? = null) : CSModel(parent), CSProperty<T> {
    protected abstract val property: CSProperty<T>
    override fun value(newValue: T, fire: Boolean) = property.value(newValue, fire)
    override fun onChange(function: (T) -> Unit) = property.onChange(function)
    override var value: T
        get() = property.value
        set(value) = value(value)

    override fun fireChange() = property.fireChange()
}