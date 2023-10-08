package renetik.android.event.util

import kotlin.time.Duration
import renetik.android.core.java.lang.isThreadMain
import renetik.android.core.kotlin.primitives.min
import renetik.android.core.lang.CSHandler.main
import renetik.android.core.lang.send
import renetik.android.event.common.CSHasDestruct
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import renetik.android.event.registration.cancel
import renetik.android.event.registration.later
import renetik.android.event.registration.plus

object CSLater {
    //TODO: Move to CSHasDestruct+MainHandler
    inline fun CSHasDestruct.later(crossinline function: () -> Unit) =
        main.send { if (!isDestructed) function() }

    //TODO: Move to CSHasDestruct+MainHandler
    inline fun CSHasDestruct.onMain(crossinline function: () -> Unit) =
        if (isThreadMain) function() else main.send { if (!isDestructed) function() }

    //TODO: Move to CSHasRegistrations+MainHandler
    inline fun CSHasRegistrations.later(
        after: Int, crossinline function: () -> Unit
    ): CSRegistration {
        lateinit var registration: CSRegistration
        registration = this + main.later(after.min(10)) { cancel(registration); function() }
        return CSRegistration { if (!registration.isCanceled) cancel(registration) }
    }

    //TODO: Move to CSHasRegistrations+MainHandler
    inline fun CSHasRegistrations.later(
        after: Duration, crossinline function: () -> Unit
    ) = later(after.inWholeMilliseconds.toInt(), function)
}