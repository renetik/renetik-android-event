package renetik.android.event.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import renetik.android.core.lang.CSFunc
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import renetik.android.event.registration.launch
import renetik.android.event.registration.onCancel
import renetik.android.event.registration.plus
import kotlin.time.Duration

class CSDebouncer(
    parent: CSHasRegistrations,
    dispatcher: CoroutineDispatcher = Main,
    action: suspend () -> Unit,
    private val after: Duration = Duration.ZERO,
) : CSFunc {

    companion object {
        fun CSHasRegistrations.debouncer(
            after: Duration, function: suspend () -> Unit
        ) =
            CSDebouncer(this, Main, function, after)

        fun CSHasRegistrations.debouncer(
            dispatcher: CoroutineDispatcher = Main,
            after: Duration, function: suspend () -> Unit
        ) =
            CSDebouncer(this, dispatcher, function, after)

        fun CSHasRegistrations.debouncer(
            dispatcher: CoroutineDispatcher = Main, function: suspend () -> Unit
        ) =
            CSDebouncer(this, dispatcher, function)

        fun CSHasRegistrations.debouncer(
            function: suspend () -> Unit
        ) =
            CSDebouncer(this, Main, function)
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
                action()
            }
        }.onCancel { this.action = null }
        parent + CSRegistration(onCancel = {
            this.action = null
        })
    }

    override operator fun invoke() {
        flow.tryEmit(Unit)
    }
}