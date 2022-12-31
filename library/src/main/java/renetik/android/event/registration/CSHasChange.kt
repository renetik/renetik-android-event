package renetik.android.event.registration

import renetik.android.core.lang.void

interface CSHasChange<Argument> {
    companion object {
        fun onChange(
            vararg hasChanges: CSHasChange<*>, function: () -> void): CSRegistration {
            val registrations = CSRegistrationsList(this)
            hasChanges.forEach { registrations.register(it.onChange(function)) }
            return registrations
        }
    }

    fun onChange(function: (Argument) -> void): CSRegistration
}