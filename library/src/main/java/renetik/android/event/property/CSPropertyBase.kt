package renetik.android.event.property

import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.common.CSHasDestruct
import renetik.android.event.common.CSModel
import renetik.android.event.paused

abstract class CSPropertyBase<T>(
    parent: CSHasDestruct? = null,
    val onChange: ((value: T) -> Unit)? = null
) : CSModel(parent), CSProperty<T> {

    constructor(onChange: ((value: T) -> Unit)? = null)
            : this(parent = null, onChange = onChange)

    val eventChange by lazy { event<T>() }

    override fun onChange(function: (T) -> Unit) = eventChange.listen(function)

    override fun toString() = super.toString() + "$value"

    override fun fireChange() = value.let {
        onChange?.invoke(it)
        eventChange.fire(it)
    }

    open fun onValueChanged(newValue: T, fire: Boolean = true) {
        isChanged = true
        if (fire) fireChange()
    }

    private var isChanged = false

    override fun paused(function: () -> Unit) {
        eventChange.paused {
            isChanged = false
            function()
        }
        if (isChanged) fireChange()
        isChanged = false
    }
}