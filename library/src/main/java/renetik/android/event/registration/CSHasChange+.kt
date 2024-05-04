package renetik.android.event.registration

import kotlinx.coroutines.suspendCancellableCoroutine
import renetik.android.core.lang.Func
import renetik.android.event.common.CSLaterOnceFunc.Companion.laterOnce

suspend fun <T> CSHasChange<T>.waitForChange(): T = suspendCancellableCoroutine { coroutine ->
    var registration: CSRegistration? = null
    registration = onChange {
        registration?.cancel()
        registration = null
        coroutine.resumeWith(Result.success(it))
    }
    coroutine.invokeOnCancellation { registration?.cancel() }
}

inline fun <Argument> CSHasChange<Argument>.onChange(
    crossinline function: () -> Unit,
): CSRegistration = onChange { _ -> function() }

inline fun CSHasChange<Unit>.action(
    crossinline function: () -> Unit,
): CSRegistration {
    function()
    return onChange(function)
}

inline fun <Argument> CSHasChange<Argument>.onChange(
    crossinline function: (CSRegistration, Argument) -> Unit,
): CSRegistration {
    lateinit var registration: CSRegistration
    registration = onChange { function(registration, it) }
    return registration
}

inline fun <Argument> CSHasChange<Argument>.onChange(
    crossinline function: (CSRegistration) -> Unit,
): CSRegistration {
    lateinit var registration: CSRegistration
    registration = onChange { function(registration) }
    return registration
}

inline fun <Argument> CSHasChange<Argument>.onChangeLaterOnce(
    crossinline function: Func,
): CSRegistration {
    val registrations = CSRegistrationsList(this)
    val laterOnceFunction = registrations.laterOnce { function() }
    registrations.register(onChange { laterOnceFunction() })
    return registrations
}
