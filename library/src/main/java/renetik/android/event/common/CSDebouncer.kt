package renetik.android.event.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import renetik.android.core.lang.CSFunc
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.launch
import renetik.android.event.registration.onCancel
import kotlin.time.Duration

/**
 * A debouncer class to delay action execution until a period of inactivity.
 *
 * The purpose of a debouncer is to consolidate multiple quick, successive calls
 * into a single action execution after a specified delay (the "debounce delay").
 * This is useful in scenarios such as handling UI events (button clicks, text input)
 * to avoid invoking costly operations repeatedly.
 *
 * In this implementation, the debouncer uses a MutableSharedFlow to collect
 * invocations and schedule the action using coroutines. The action will be
 * executed only when no new invocations are received for the specified delay.
 */
class CSDebouncer(
    parent: CSHasRegistrations,
    dispatcher: CoroutineDispatcher = Main,
    action: suspend () -> Unit,
    private val after: Duration = Duration.ZERO,
) : CSFunc {

    companion object {
        fun CSHasRegistrations.debouncer(
            after: Duration, function: suspend () -> Unit
        ) = CSDebouncer(this, Main, function, after)

        fun CSHasRegistrations.debouncer(
            dispatcher: CoroutineDispatcher = Main,
            after: Duration, function: suspend () -> Unit
        ) = CSDebouncer(this, dispatcher, function, after)

        fun CSHasRegistrations.debouncer(
            dispatcher: CoroutineDispatcher = Main, function: suspend () -> Unit
        ) = CSDebouncer(this, dispatcher, function)

        fun CSHasRegistrations.debouncer(
            function: suspend () -> Unit
        ) = CSDebouncer(this, Main, function)
    }

    @Volatile
    private var action: (suspend () -> Unit)? = action

    private val flow = MutableSharedFlow<Unit>(
        replay = 1, extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        parent.launch(dispatcher) {
            flow.collectLatest {
                delay(after)
                this.action?.invoke()
            }
        }.onCancel { this.action = null }
    }

    override operator fun invoke() {
        flow.tryEmit(Unit)
    }
}