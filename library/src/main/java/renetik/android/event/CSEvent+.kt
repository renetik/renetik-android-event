package renetik.android.event

import androidx.annotation.UiThread
import renetik.android.event.registration.CSRegistration

fun CSEvent<Unit>.fire() = apply { fire(Unit) }

inline fun CSEvent<Unit>.listen(
	@UiThread crossinline function: () -> Unit) = listen { _, _ -> function() }

inline fun CSEvent<*>.action(
	@UiThread crossinline function: () -> Unit): CSRegistration {
	function()
	return this.listen { _, _ -> function() }
}

inline fun <T> CSEvent<T>.listen(
	@UiThread crossinline function: (registration: CSRegistration,
	                                 argument: T) -> Unit): CSRegistration {
	lateinit var registration: CSRegistration
	registration = listen { function(registration, it) }
	return registration
}

inline fun <T> CSEvent<T>.listenOnce(
	@UiThread crossinline listener: (argument: T) -> Unit) =
	listen { registration, argument ->
		registration.cancel()
		listener(argument)
	}