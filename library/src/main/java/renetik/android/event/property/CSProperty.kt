package renetik.android.event.property

import renetik.android.core.lang.variable.CSVariable
import renetik.android.event.registration.CSRegistration

interface CSProperty<T> : CSVariable<T> {

    fun value(newValue: T, fire: Boolean = true)

    fun onChange(function: (T) -> Unit): CSRegistration

    /**
     * Signal change with current value
     */
    fun fireChange()
}