package renetik.android.event.registration

import renetik.android.core.lang.variable.CSVariable.Companion.variable
import renetik.android.core.lang.void

typealias HasChange = CSHasChange<void>

inline fun <Argument> CSHasChange<Argument>.onChange(
    crossinline function: () -> void): CSRegistration =
    onChange { _ -> function() }

inline fun CSHasChange<void>.action(crossinline function: () -> void): CSRegistration {
    function()
    return onChange(function)
}

inline fun <Argument> CSHasChange<Argument>.onChange(
    crossinline function: (CSRegistration, Argument) -> void): CSRegistration {
    lateinit var registration: CSRegistration
    registration = onChange { function(registration, it) }
    return registration
}

inline fun <Argument> CSHasChange<Argument>.onChange(
    crossinline function: (CSRegistration) -> void): CSRegistration {
    lateinit var registration: CSRegistration
    registration = onChange { function(registration) }
    return registration
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