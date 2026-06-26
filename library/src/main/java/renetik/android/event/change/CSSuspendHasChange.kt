package renetik.android.event.change

import renetik.android.event.dispatch.*
import renetik.android.event.registration.*
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

interface CSSuspendHasChange<Argument> {
    fun onChange(function: suspend (Argument) -> Unit): CSRegistration
}