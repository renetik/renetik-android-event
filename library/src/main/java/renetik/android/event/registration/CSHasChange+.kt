package renetik.android.event.registration

import kotlinx.coroutines.suspendCancellableCoroutine
import renetik.android.core.lang.Func
import renetik.android.core.lang.Quadruple
import renetik.android.event.common.CSLaterOnceFunc.Companion.laterOnceFunc

suspend fun <T> CSHasChange<T>.waitForChange(): T =
    suspendCancellableCoroutine { coroutine ->
        var registration: CSRegistration? = null
        registration = onChange {
            registration?.cancel()
            registration = null
            coroutine.resumeWith(Result.success(it))
        }
        coroutine.invokeOnCancellation { registration?.cancel() }
    }

infix fun CSHasChange<*>.or(other: CSHasChange<*>): CSHasChange<Unit> {
    val self = this
    return object : CSHasChange<Unit> {
        override fun onChange(function: (Unit) -> Unit): CSRegistration {
            return CSRegistration(
                self.onChange { function(Unit) },
                other.onChange { function(Unit) },
            )
        }
    }
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

inline fun <Argument> CSHasChange<Argument>.onChangeOnce(
    crossinline function: () -> Unit,
): CSRegistration = onChange { registration: CSRegistration ->
    registration.cancel()
    function()
}

inline fun <Argument> CSHasChange<Argument>.onChangeLaterOnce(
    crossinline function: Func,
): CSRegistration {
    val registrations = CSRegistrationsMap(this)
    val laterOnceFunction = registrations.laterOnceFunc { function() }
    registrations.register(onChange { laterOnceFunction() })
    return registrations
}

fun Pair<CSHasChange<*>, CSHasChange<*>>.onChange(
    onChange: () -> Unit
): CSRegistration = CSRegistration(
    first.onChange(onChange), second.onChange(onChange)
)

fun Triple<CSHasChange<*>, CSHasChange<*>, CSHasChange<*>>.onChange(
    onChange: () -> Unit
): CSRegistration = CSRegistration(
    first.onChange(onChange), second.onChange(onChange),
    third.onChange(onChange)
)

fun Quadruple<CSHasChange<*>, CSHasChange<*>, CSHasChange<*>, CSHasChange<*>>.onChange(
    onChange: () -> Unit
): CSRegistration = CSRegistration(
    first.onChange(onChange), second.onChange(onChange),
    third.onChange(onChange), fourth.onChange(onChange)
)

