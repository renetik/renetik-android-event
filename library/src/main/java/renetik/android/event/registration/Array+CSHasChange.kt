package renetik.android.event.registration

import renetik.android.core.kotlin.className
import renetik.android.core.lang.Fun
import renetik.android.event.common.CSDebouncer.Companion.debouncer

inline fun <T : CSHasChange<*>> Array<T>.onChangeLater(
    crossinline function: Fun
): CSRegistration {
    val registrations = CSRegistrationsMap(className)
    val laterOnceFunction = registrations.debouncer { function() }
    forEach { registrations.register(it.onChange { laterOnceFunction() }) }
    return registrations
}

inline fun <T : CSHasChange<*>> Array<T>.onChange(
    crossinline function: Fun
): CSRegistration {
    val registrations = CSRegistrationsMap(className)
    forEach { registrations.register(it.onChange(function)) }
    return registrations
}

inline fun <T : CSHasChange<*>> Array<T>.actionOnChangeLater(
    crossinline function: Fun
): CSRegistration {
    function()
    return onChangeLater(function)
}