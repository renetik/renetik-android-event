package renetik.android.event.common

import kotlinx.coroutines.delay
import renetik.android.core.lang.CSFunc
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.cancel
import renetik.android.event.registration.launch
import kotlin.time.Duration

class CSLaterOnceFunc(
    private val parent: CSHasRegistrations,
    private val function: () -> Unit,
    val after: Int = 0,
) : CSFunc {

    companion object {
        fun CSHasRegistrations.laterOnce(after: Duration, function: () -> Unit) =
            CSLaterOnceFunc(this, function, after.inWholeMilliseconds.toInt())

        fun CSHasRegistrations.laterOnce(after: Int = 0, function: () -> Unit) =
            CSLaterOnceFunc(this, function, after)
    }

    var registration: CSRegistration? = null
    override operator fun invoke() {
        parent.cancel(registration)
        registration = parent.launch { delay(after.toLong()); function() }
    }
}

//class CSLaterOnceFuncOld(
//    private val parent: CSHasRegistrations,
//    private val function: () -> Unit,
//    val after: Int = 0,
//) : CSFunc {
//
//    companion object {
//        fun CSHasRegistrations.laterOnce(after: Int = 0, function: () -> Unit) =
//            CSLaterOnceFunc(this, function, after)
//    }
//
//    private var isInvoking = false
//
//    override operator fun invoke() {
//        if (!isInvoking) {
//            isInvoking = true
//            parent.registerLater(after) {
//                function()
//                isInvoking = false
//            }
//        }
//    }
//}