package renetik.android.event.common

import renetik.android.core.lang.CSFunc
import renetik.android.core.lang.void
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.later

class CSLaterOnceFunc(
    private val parent: CSHasRegistrations,
    private val function: () -> void) : CSFunc {

    companion object {
        fun CSHasRegistrations.laterOnce(function: () -> void) =
            CSLaterOnceFunc(this, function)
    }

    private var isInvoking = false

    override operator fun invoke() {
        if (!isInvoking) {
            isInvoking = true
            parent.later {
                function()
                isInvoking = false
            }
        }
    }
}
