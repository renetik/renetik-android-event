package renetik.android.event.property

import renetik.android.core.kotlin.run
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.owner.CSEventOwnerHasDestroyBase
import renetik.android.event.owner.CSHasDestroy

abstract class CSEventPropertyBase<T>(
    parent: CSHasDestroy? = null,
    val onChange: ((value: T) -> Unit)? = null)
    : CSEventOwnerHasDestroyBase(parent), CSEventProperty<T> {

    constructor(onApply: ((value: T) -> Unit)? = null)
            : this(parent = null, onChange = onApply)

    val eventChange = event<T>()

    override fun onChange(function: (T) -> Unit) = eventChange.listen(function)

    override fun toString() = super.toString() + "$value"

    override fun invokeChange() {
        value?.run {
            onChange?.invoke(it)
            eventChange.fire(it)
        }
    }

    open fun onValueChanged(newValue: T, fire: Boolean = true) {
        onChange?.invoke(newValue)
        if (fire) eventChange.fire(newValue)
    }
}