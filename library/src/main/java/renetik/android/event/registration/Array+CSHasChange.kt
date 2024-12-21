package renetik.android.event.registration

import renetik.android.core.lang.Func
import renetik.android.event.common.CSLaterOnceFunc.Companion.laterOnceFunc

inline fun <T : CSHasChange<*>> Array<T>.onChangeLater(
    crossinline function: Func
): CSRegistration {
    val registrations = CSRegistrationsMap(this)
    val laterOnceFunction = registrations.laterOnceFunc { function() }
    forEach { registrations.register(it.onChange { laterOnceFunction() }) }
    return registrations
}

inline fun <T : CSHasChange<*>> Array<T>.onChange(
    crossinline function: Func
): CSRegistration {
    val registrations = CSRegistrationsMap(this)
    forEach { registrations.register(it.onChange(function)) }
    return registrations
}

inline fun <T : CSHasChange<*>> Array<T>.actionOnChangeLater(
    crossinline function: Func
): CSRegistration {
    function()
    return onChangeLater(function)
}