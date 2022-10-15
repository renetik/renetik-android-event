package renetik.android.event.registration

import renetik.android.core.lang.void

interface CSHasChange<Argument> {
    fun onChange(function: (Argument) -> void): CSRegistration
}