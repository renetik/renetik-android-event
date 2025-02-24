package renetik.android.event.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import renetik.android.core.lang.CSFunc
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.launch
import kotlin.time.Duration

/**
 * 1. On the first call, [action] is executed after [after].
 * 2. Any calls arriving while waiting are ignored.
 */
class Throttler(
    parent: CSHasRegistrations,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val action: suspend () -> Unit,
    private val after: Duration = Duration.ZERO,
) : CSFunc {
    companion object {
        fun CSHasRegistrations.throttler(
            after: Duration, function: suspend () -> Unit) =
            Throttler(this, Main, function, after)

        fun CSHasRegistrations.throttler(
            dispatcher: CoroutineDispatcher = Main,
            after: Duration, function: suspend () -> Unit) =
            Throttler(this, dispatcher, function, after)

        fun CSHasRegistrations.throttler(
            dispatcher: CoroutineDispatcher = Main, function: suspend () -> Unit) =
            Throttler(this, dispatcher, function)

        fun CSHasRegistrations.throttler(
            function: suspend () -> Unit) =
            Throttler(this, Main, function)
    }

    private val flow = MutableSharedFlow<Unit>(
        replay = 0, extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        parent.launch(dispatcher) {
            flow.collect {
                action()
                if (after > Duration.ZERO) delay(after)
            }
        }
    }

    override operator fun invoke() {
        flow.tryEmit(Unit)
    }
}