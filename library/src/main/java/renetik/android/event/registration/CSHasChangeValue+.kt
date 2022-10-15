package renetik.android.event.registration

import renetik.android.core.kotlin.then
import renetik.android.core.lang.variable.CSVariable.Companion.variable
import renetik.android.core.lang.void
import renetik.android.event.property.CSProperty.Companion.property
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

infix fun CSHasChangeValue<Boolean>.or(other: CSHasChangeValue<Boolean>)
        : CSHasChangeValue<Boolean> {
    val property = property(false)
    fun updateProperty() = property.value(value or other.value)
    var registration: CSRegistration? = null
    fun registerEvents() {
        registration = CSRegistration(onChange { updateProperty() },
            other.onChange { updateProperty() })
    }
    updateProperty()

    var registrationCount by variable(0) {
        if (it == 0) registration!!.cancel().then { registration = null }
        else if (registration == null) registerEvents()
    }
    return object : CSHasChangeValue<Boolean> {
        override val value get() = property.value
        override fun onChange(function: (Boolean) -> void): CSRegistration {
            registrationCount++
            val propertyOnChange = property.onChange { function(value) }
            return CSRegistration {
                registrationCount--
                propertyOnChange.cancel()
            }
        }
    }
}