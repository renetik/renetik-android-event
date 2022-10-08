package renetik.android.event.registration

import renetik.android.core.lang.variable.CSVariable.Companion.variable
import renetik.android.core.lang.void

/**
 * Not used yet ... just an idea as this pattern is in various places..
 */

typealias HasChange = CSHasChange<void>

interface CSHasChange<Argument> {
    fun onChange(function: (Argument) -> void): CSRegistration
}

inline fun <Argument> CSHasChange<Argument>.onChange(
    crossinline function: () -> void): CSRegistration =
    onChange { _ -> function() }

inline fun CSHasChange<void>.action(crossinline function: () -> void): CSRegistration {
    function()
    return onChange(function)
}

inline fun <Argument> CSHasChange<Argument>.onChangeOnce(
    crossinline listener: (Argument) -> void): CSRegistration {
    var registration: CSRegistration by variable()
    registration = onChange { argument ->
        registration.cancel()
        listener(argument)
    }
    return registration
}

inline fun HasChange.onChangeOnce(
    crossinline listener: () -> void): CSRegistration =
    onChangeOnce { _ -> listener() }
