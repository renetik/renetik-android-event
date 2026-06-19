package renetik.android.event.registration

import renetik.android.core.lang.ArgFun
import renetik.android.event.CSEvent.Companion.event

internal abstract class CSHasChangeValueBase<Return>(
    val parent: CSHasRegistrations?,
    val onChange: ArgFun<Return>? = null
) : CSHasChangeValue<Return> {
    abstract override var value: Return
    val eventChange by lazy { event<Return>() }
    override fun onChange(function: (Return) -> Unit) = eventChange.listen(function)

    @Synchronized
    open fun value(newValue: Return) {
        if (value == newValue) return
        value = newValue
        onValueChanged(newValue)
    }

    fun onValueChanged(newValue: Return) {
        onChange?.invoke(newValue)
        eventChange.fire(newValue)
    }

    operator fun plus(registration: CSRegistration): CSRegistration? =
        parent?.register(registration)
}
