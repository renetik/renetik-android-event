package renetik.android.event.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.launch
import kotlin.time.Duration

/**
 * A generic throttler that limits how often an action is executed.
 *
 * The throttler accepts parameterized calls and ensures that the provided action is not
 * triggered more than once within a specified period (the throttle interval). Once an action
 * is executed, any subsequent invocations received within the throttle delay are ignored or
 * queued based on the throttling mechanism.
 *
 * This class uses a MutableSharedFlow to collect parameterized invocations and processes each
 * one sequentially. After executing the action with the given parameter, it waits for the defined
 * throttle period (if any) before processing the next incoming event.
 *
 * Use cases for a throttler include limiting high-frequency events (like scroll or resize events)
 * so that processing happens only once every few milliseconds or seconds, even if many events occur.
 */
class Throttler<T>(
    parent: CSHasRegistrations,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val action: suspend (T) -> Unit,
    private val after: Duration = Duration.ZERO,
) : (T) -> Unit {  // Now the class itself is a function that takes T
    companion object {
        fun CSHasRegistrations.throttler(
            after: Duration, function: suspend () -> Unit) =
            Throttler<Unit>(this, Main, { function() }, after)

        fun CSHasRegistrations.throttler(
            dispatcher: CoroutineDispatcher = Main,
            after: Duration, function: suspend () -> Unit) =
            Throttler<Unit>(this, dispatcher, { function() }, after)

        fun CSHasRegistrations.throttler(
            dispatcher: CoroutineDispatcher = Main, function: suspend () -> Unit) =
            Throttler<Unit>(this, dispatcher, { function() })

        fun CSHasRegistrations.throttler(
            function: suspend () -> Unit) =
            Throttler<Unit>(this, Main, { function() })

        fun <T> CSHasRegistrations.throttler(
            after: Duration, function: suspend (T) -> Unit
        ) = Throttler(this, Main, function, after)

        fun <T> CSHasRegistrations.throttler(
            dispatcher: CoroutineDispatcher = Main,
            after: Duration, function: suspend (T) -> Unit
        ) = Throttler(this, dispatcher, function, after)

        fun <T> CSHasRegistrations.throttler(
            dispatcher: CoroutineDispatcher = Main, function: suspend (T) -> Unit
        ) = Throttler(this, dispatcher, function)

        fun <T> CSHasRegistrations.throttler(
            function: suspend (T) -> Unit
        ) = Throttler(this, Main, function)

        operator fun Throttler<Unit>.invoke() = invoke(Unit)
    }

    private val flow = MutableSharedFlow<T>(
        replay = 0, extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        parent.launch(dispatcher) {
            flow.collect { param ->
                action(param)
                if (after > Duration.ZERO) delay(after)
            }
        }
    }

    override operator fun invoke(param: T) {
        flow.tryEmit(param)
    }
}