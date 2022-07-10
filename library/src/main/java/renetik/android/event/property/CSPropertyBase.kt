package renetik.android.event.property

import renetik.android.core.kotlin.run
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.registrations.CSHasRegistrationsHasDestroyBase
import renetik.android.event.registrations.CSHasDestroy

abstract class CSPropertyBase<T>(
    parent: CSHasDestroy? = null,
    val onChange: ((value: T) -> Unit)? = null)
    : CSHasRegistrationsHasDestroyBase(parent), CSProperty<T> {

    constructor(onApply: ((value: T) -> Unit)? = null)
            : this(parent = null, onChange = onApply)

    val eventChange = event<T>()

    override fun onChange(function: (T) -> Unit) = eventChange.listen(function)

    override fun toString() = super.toString() + "$value"

    override fun fireChange() {
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