package renetik.android.event.property

import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.common.CSHasDestroy
import renetik.android.event.common.CSModel

abstract class CSPropertyBase<T>(
    parent: CSHasDestroy? = null,
    val onChange: ((value: T) -> Unit)? = null)
    : CSModel(parent), CSProperty<T> {

    constructor(onApply: ((value: T) -> Unit)? = null)
            : this(parent = null, onChange = onApply)

    val eventChange = event<T>()

    override fun onChange(function: (T) -> Unit) = eventChange.listen(function)

    override fun toString() = super.toString() + "$value"

    override fun fireChange() = value.let {
        onChange?.invoke(it)
        eventChange.fire(it)
    }

    open fun onValueChanged(newValue: T, fire: Boolean = true) {
        if (fire) fireChange()
    }
}