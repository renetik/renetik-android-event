package renetik.android.event.registration

import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.suspendCancellableCoroutine
import renetik.android.core.lang.Func
import renetik.android.core.lang.Quadruple
import renetik.android.core.lang.value.CSValue
import renetik.android.event.CSEvent
import renetik.android.event.common.CSLaterOnceFunc.Companion.laterOnceFunc
import renetik.android.event.fire
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO

fun <T> CSHasChange<T>.onChangeLaunch(
    function: suspend (T) -> Unit
): CSRegistration {
    val registrations = CSRegistrationsMap(this)
    registrations + onChange { param ->
        registrations + Main.launch { function(param) }
    }
    return registrations
}

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
        override fun onChange(function: (Unit) -> Unit) = CSRegistration(
            self.onChange { function(Unit) },
            other.onChange { function(Unit) },
        )
    }
}

infix fun CSHasChange<*>.and(other: CSValue<Boolean>): CSHasChange<Unit> {
    val self = this
    return object : CSHasChange<Unit> {
        override fun onChange(function: (Unit) -> Unit): CSRegistration =
            CSRegistration(self.onChange { if (other.value) function(Unit) })
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
    after: Duration,
    crossinline function: Func,
): CSRegistration {
    val registrations = CSRegistrationsMap(this)
    val laterOnceFunction = registrations.laterOnceFunc(after) { function() }
    registrations.register(onChange { laterOnceFunction() })
    return registrations
}

inline fun <Argument> CSHasChange<Argument>.onChangeLaterOnce(
    crossinline function: Func,
) = onChangeLaterOnce(ZERO, function)

fun Pair<CSHasChange<*>, CSHasChange<*>>.onChange(
    onChange: () -> Unit
): CSRegistration = CSRegistration(
    first.onChange(onChange), second.onChange(onChange)
)

fun Pair<CSHasChange<*>, CSHasChange<*>>.event(
    parent: CSHasRegistrations? = null
): CSEvent<Unit> = CSEvent.event().also { event ->
    first.onChange(event::fire).also { parent?.register(it) }
    second.onChange(event::fire).also { parent?.register(it) }
}

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

