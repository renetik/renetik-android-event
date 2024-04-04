package renetik.android.event

import renetik.android.core.lang.Func
import renetik.android.event.property.CSProperty
import renetik.android.event.registration.CSHasChangeValue
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.plus

fun CSEvent<Unit>.fire() = apply { fire(Unit) }

operator fun CSEvent<Unit>.invoke() = apply { fire(Unit) }

@JvmName("listenNoArg")
fun CSEvent<*>.listen(function: Func): CSRegistration = listen { _, _ -> function() }

inline fun CSEvent<Unit>.listen(crossinline function: Func) = listen { _, _ -> function() }

inline fun CSEvent<*>.action(crossinline function: () -> Unit): CSRegistration {
    function()
    return this.listen { _, _ -> function() }
}

inline fun CSEvent<*>.action(crossinline function: (CSRegistration) -> Unit): CSRegistration {
    val registration = this.listen { registration, _ -> function(registration) }
    function(registration)
    return registration
}

inline fun <T> CSEvent<T>.listen(
    crossinline function: (registration: CSRegistration, argument: T) -> Unit
): CSRegistration {
    lateinit var registration: CSRegistration
    registration = listen { function(registration, it) }
    return registration
}

inline fun <T> CSEvent<T>.listenOnce(
    crossinline listener: (argument: T) -> Unit
): CSRegistration =
    listen { registration, argument ->
        registration.cancel()
        listener(argument)
    }

fun CSEvent<*>.paused(function: Func) {
    pause()
    function()
    resume()
}

fun <EventArg, PropertyType> CSEvent<EventArg>.computedProperty(
    parent: CSHasRegistrations, from: () -> PropertyType
): CSHasChangeValue<PropertyType> {
    val property = CSProperty.property(from())
    parent + listen { property.value = from() }
    return property
}