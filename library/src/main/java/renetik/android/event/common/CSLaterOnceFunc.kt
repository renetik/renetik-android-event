package renetik.android.event.common

import renetik.android.core.lang.CSFunc
import renetik.android.core.lang.void
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.registerLater

class CSLaterOnceFunc(
    private val parent: CSHasRegistrations,
    private val function: () -> Unit,
    val after: Int = 0) : CSFunc {

    companion object {
        fun CSHasRegistrations.laterOnce(function: () -> void) =
            CSLaterOnceFunc(this, function)

        fun CSHasRegistrations.laterOnce(after: Int, function: () -> void) =
            CSLaterOnceFunc(this, function, after)
    }

    private var isInvoking = false

    override operator fun invoke() {
        if (!isInvoking) {
            isInvoking = true
            parent.registerLater(after) {
                function()
                isInvoking = false
            }
        }
    }
}
