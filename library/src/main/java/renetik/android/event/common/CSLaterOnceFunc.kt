package renetik.android.event.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import renetik.android.core.lang.CSFunc
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.launch
import kotlin.time.Duration

class CSLaterOnceFunc(
    private val parent: CSHasRegistrations,
    private val dispatcher: CoroutineDispatcher = Main,
    val function: suspend () -> Unit,
    private val after: Duration = Duration.ZERO,
) : CSFunc {

    companion object {
        fun CSHasRegistrations.debouncer(
            after: Duration, function: suspend () -> Unit) =
            Debouncer(this, Main, function, after)

        fun CSHasRegistrations.debouncer(
            dispatcher: CoroutineDispatcher = Main,
            after: Duration, function: suspend () -> Unit) =
            Debouncer(this, dispatcher, function, after)

        fun CSHasRegistrations.debouncer(
            dispatcher: CoroutineDispatcher = Main, function: suspend () -> Unit) =
            Debouncer(this, dispatcher, function)

        fun CSHasRegistrations.debouncer(
            function: suspend () -> Unit) =
            Debouncer(this, Main, function)
    }

    var registration: CSRegistration? = null
    override operator fun invoke() {
        registration?.cancel()
        registration = parent.launch(dispatcher) { delay(after); function() }
    }
}