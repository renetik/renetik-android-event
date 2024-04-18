package renetik.android.event.common

import renetik.android.core.lang.CSFunc
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.CSRegistration
import renetik.android.event.util.CSLater.later
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
        registration?.cancel()
        registration = parent.later(after, function)
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