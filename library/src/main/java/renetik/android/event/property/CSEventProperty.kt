package renetik.android.event.property

import renetik.android.event.registration.CSRegistration
import renetik.android.core.lang.property.CSProperty

interface CSEventProperty<T> : CSProperty<T> {
    fun value(newValue: T, fire: Boolean = true)
    fun onChange(function: (T) -> Unit): CSRegistration
}