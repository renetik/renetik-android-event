package renetik.android.event.registration

import renetik.android.core.lang.Func
import renetik.android.event.common.CSLaterOnceFunc.Companion.laterOnceFunc

inline fun <T : CSHasChange<*>> List<T>.onChange(
    crossinline function: Func
): CSRegistration {
    val registrations = CSRegistrationsList(this)
    forEach {
        registrations.register(it.onChange {
            if (registrations.isActive) function()
        })
    }
    return registrations
}

inline fun <T : CSHasChange<*>> List<T>.onChangeLaterOnce(
    crossinline function: Func
): CSRegistrations {
    val registrations = CSRegistrationsMap(this)
    val laterOnceFunction = registrations.laterOnceFunc({ function() })
    forEach { registrations.register(it.onChange { laterOnceFunction() }) }
    return registrations
}

inline fun <T : CSHasChange<*>> List<T>.action(
    crossinline function: Func
): CSRegistration {
    function()
    return onChange(function)
}