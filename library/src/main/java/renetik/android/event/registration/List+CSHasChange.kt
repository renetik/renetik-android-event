package renetik.android.event.registration

import renetik.android.core.lang.Func
import renetik.android.event.common.CSLaterOnceFunc.Companion.laterOnce

inline fun <T : CSHasChange<*>> List<T>.onChange(
    crossinline function: Func
): CSRegistration {
    val registrations = CSRegistrationsList(this)
    forEach { registrations.register(it.onChange { function() }) }
    return registrations
}

inline fun <T : CSHasChange<*>> List<T>.onChangeLater(
    crossinline function: Func
): CSRegistration {
    val registrations = CSRegistrationsList(this)
    val laterOnceFunction = registrations.laterOnce { function() }
    forEach { registrations.register(it.onChange { laterOnceFunction() }) }
    return registrations
}

inline fun <T : CSHasChange<*>> List<T>.actionOnChangeLater(
    crossinline function: Func
): CSRegistration {
    function()
    return onChangeLater(function)
}

inline fun <T : CSHasChange<*>> List<T>.action(
    crossinline function: Func
): CSRegistration {
    function()
    return onChange(function)
}