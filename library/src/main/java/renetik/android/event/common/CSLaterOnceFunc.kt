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
    private val after: Int = 0,
) : CSFunc {

    companion object {
        fun CSHasRegistrations.laterOnceFunc(
            after: Duration, function: suspend () -> Unit) =
            CSLaterOnceFunc(this, Main, function, after.inWholeMilliseconds.toInt())

        fun CSHasRegistrations.laterOnceFunc(
            dispatcher: CoroutineDispatcher = Main,
            after: Duration, function: suspend () -> Unit) =
            CSLaterOnceFunc(this, dispatcher, function, after.inWholeMilliseconds.toInt())

        fun CSHasRegistrations.laterOnceFunc(
            dispatcher: CoroutineDispatcher = Main, function: suspend () -> Unit) =
            CSLaterOnceFunc(this, dispatcher, function)

        fun CSHasRegistrations.laterOnceFunc(
            function: suspend () -> Unit) =
            CSLaterOnceFunc(this, Main, function)
    }

    var registration: CSRegistration? = null
    override operator fun invoke() {
        registration?.cancel()
        registration = parent.launch(dispatcher) { delay(after.toLong()); function() }
    }
}