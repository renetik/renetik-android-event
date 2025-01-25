package renetik.android.event.common

import kotlinx.coroutines.delay
import renetik.android.core.lang.CSFunc
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.launch
import kotlin.time.Duration

class CSLaterOnceFunc(
    private val parent: CSHasRegistrations,
    val function: suspend () -> Unit,
    private val after: Int = 0,
) : CSFunc {

    companion object {
        fun CSHasRegistrations.laterOnceFunc(after: Duration, function: suspend () -> Unit) =
            CSLaterOnceFunc(this, function, after.inWholeMilliseconds.toInt())

        fun CSHasRegistrations.laterOnceFunc(function: suspend () -> Unit) =
            CSLaterOnceFunc(this, function)
    }

    var registration: CSRegistration? = null
    override operator fun invoke() {
        registration?.cancel()
        registration = parent.launch { delay(after.toLong()); function() }
    }
}