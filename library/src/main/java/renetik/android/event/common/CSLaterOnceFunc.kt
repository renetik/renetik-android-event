package renetik.android.event.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import renetik.android.core.lang.CSFunc
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.launch
import java.lang.ref.WeakReference
import kotlin.time.Duration

/**
 * 1. On the first call, [action] is executed after [after].
 * 2. Any calls arriving while waiting are ignored.
 */
class Throttler(
    parent: CSHasRegistrations,
    dispatcher: CoroutineDispatcher = Main,
    private val after: Duration = Duration.ZERO,
    action: suspend () -> Unit,
) : CSFunc {
    private val action = WeakReference(action)
    private val flow = MutableSharedFlow<Unit>(
        replay = 0, extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        parent.launch(dispatcher) {
            flow.collect {
                this.action.get()?.invoke()
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
    action: suspend () -> Unit,
    private val after: Duration = Duration.ZERO,
) : CSFunc {
    private val action = WeakReference(action)

    private val flow = MutableSharedFlow<Unit>(
        replay = 1, extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        parent.launch(dispatcher) {
            flow.collectLatest {
                delay(after)
                this.action.get()?.invoke()
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
    action: suspend () -> Unit,
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

    private val action = WeakReference(action)

    var registration: CSRegistration? = null
    override operator fun invoke() {
        registration?.cancel()
        registration = parent.launch(dispatcher) {
            delay(after)
            this.action.get()?.invoke()
        }
    }
}