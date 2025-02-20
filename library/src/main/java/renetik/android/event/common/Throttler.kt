package renetik.android.event.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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
    private val after: Duration = Duration.ZERO,
    private val action: suspend () -> Unit,
) : CSFunc {
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