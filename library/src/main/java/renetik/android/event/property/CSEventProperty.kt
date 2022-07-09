package renetik.android.event.property

import renetik.android.core.lang.property.CSVariable
import renetik.android.event.registration.CSRegistration

interface CSEventProperty<T> : CSVariable<T> {

    fun value(newValue: T, fire: Boolean = true)

    fun onChange(function: (T) -> Unit): CSRegistration

    /**
     * Invoke change event with current value
     */
    fun invokeChange()
}