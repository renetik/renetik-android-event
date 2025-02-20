package renetik.android.event.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import renetik.android.core.lang.CSFunc
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.launch
import renetik.android.event.registration.onCancel
import kotlin.time.Duration

class Debouncer(
    parent: CSHasRegistrations,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    action: suspend () -> Unit,
    private val after: Duration = Duration.ZERO,
) : CSFunc {

    @Volatile
    var action: (suspend () -> Unit)? = action

    private val flow = MutableSharedFlow<Unit>(
        replay = 1, extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        val sss = parent.launch(dispatcher) {
            flow.collectLatest {
                delay(after)
                action()
            }
        }
        sss.onCancel { this.action = null }
    }

    override operator fun invoke() {
        flow.tryEmit(Unit)
    }
}