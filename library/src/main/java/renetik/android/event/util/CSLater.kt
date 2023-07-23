package renetik.android.event.util

import renetik.android.core.java.lang.isThreadMain
import renetik.android.core.lang.CSHandler.mainHandler
import renetik.android.core.lang.CSHandler.postOnMain
import renetik.android.event.common.CSHasDestruct
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.later
import renetik.android.event.registration.laterEach

object CSLater {
    inline fun later(
        after: Int, crossinline function: () -> Unit
    ): CSRegistration = mainHandler.later(after) { function() }

    inline fun later(
        crossinline function: () -> Unit
    ): CSRegistration = later(5, function)

    inline fun CSHasDestruct.later(
        after: Int, crossinline function: () -> Unit
    ): CSRegistration = CSLater.later(after) { if (!isDestructed) function() }

    inline fun CSHasDestruct.later(crossinline function: () -> Unit) =
        postOnMain { if (!isDestructed) function() }

    inline fun CSHasDestruct.onMain(crossinline function: () -> Unit) =
        if (isThreadMain) function() else postOnMain { if (!isDestructed) function() }

    inline fun laterEach(
        after: Int, period: Int = after, crossinline function: () -> Unit
    ): CSRegistration = mainHandler.laterEach(after, period, function)

}