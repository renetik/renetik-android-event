package renetik.android.event.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import renetik.android.core.lang.CSFunc
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.launch
import kotlin.time.Duration

/**
 * 1. On the first call, [action] is executed after [after].
 * 2. Any calls arriving while waiting are ignored.
 */
class Throttler(
    parent: CSHasRegistrations,
    dispatcher: CoroutineDispatcher = Main,
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

class Debouncer(
    parent: CSHasRegistrations,
    dispatcher: CoroutineDispatcher = Main,
    private val action: suspend () -> Unit,
    private val after: Duration = Duration.ZERO,
) : CSFunc {
    private val flow = MutableSharedFlow<Unit>(
        replay = 1, extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        parent.launch(dispatcher) {
            flow.collectLatest {
                delay(after)
                action()
            }
        }
    }

    override operator fun invoke() {
        flow.tryEmit(Unit)
    }
}

class CSLaterOnceFunc(
    private val parent: CSHasRegistrations,
    private val dispatcher: CoroutineDispatcher = Main,
    val function: suspend () -> Unit,
    private val after: Duration = Duration.ZERO,
) : CSFunc {

    companion object {
        fun CSHasRegistrations.laterOnceFunc(
            after: Duration, function: suspend () -> Unit) =
            Debouncer(this, Main, function, after)

        fun CSHasRegistrations.laterOnceFunc(
            dispatcher: CoroutineDispatcher = Main,
            after: Duration, function: suspend () -> Unit) =
            Debouncer(this, dispatcher, function, after)

        fun CSHasRegistrations.laterOnceFunc(
            dispatcher: CoroutineDispatcher = Main, function: suspend () -> Unit) =
            Debouncer(this, dispatcher, function)

        fun CSHasRegistrations.laterOnceFunc(
            function: suspend () -> Unit) =
            Debouncer(this, Main, function)
    }

    var registration: CSRegistration? = null
    override operator fun invoke() {
        registration?.cancel()
        registration = parent.launch(dispatcher) { delay(after); function() }
    }
}