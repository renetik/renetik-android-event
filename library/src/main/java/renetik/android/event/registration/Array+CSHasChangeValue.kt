package renetik.android.event.registration

import renetik.android.core.lang.Func
import renetik.android.event.common.CSLaterOnceFunc.Companion.laterOnce
import renetik.android.event.property.CSProperty

inline fun <T : CSProperty<*>> Array<T>.onChangeLater(
    crossinline function: Func): CSRegistration {
    val registrations = CSRegistrationsList(this)
    val laterOnceFunction = registrations.laterOnce { function() }
    forEach { registrations.register(it.onChange { laterOnceFunction() }) }
    return registrations
}

inline fun <T : CSProperty<*>> Array<T>.actionOnChangeLater(
    crossinline function: Func): CSRegistration {
    function()
    return onChangeLater(function)
}