package renetik.android.event.util

import renetik.android.core.java.lang.isThreadMain
import renetik.android.core.lang.CSHandler.postOnMain
import renetik.android.core.lang.CSHandler.removePostedOnMain
import renetik.android.event.common.CSHasDestruct
import renetik.android.event.registration.CSFunctionRegistration
import renetik.android.event.registration.CSRegistration

object CSLater {
    inline fun later(after: Int, crossinline function: () -> Unit): CSRegistration {
        val registration = CSFunctionRegistration(function = { function() },
            onCancel = { removePostedOnMain(it.function) })
        val delay = if (after < 5) 5 else after
        postOnMain(delay, registration.function)
        return registration
    }

    fun later(function: () -> Unit) = later(5, function)

    inline fun CSHasDestruct.onMain(after: Int, crossinline function: () -> Unit) =
        postOnMain(after) { if (!isDestructed) function() }

    inline fun CSHasDestruct.onMain(crossinline function: () -> Unit) =
        if (isThreadMain) function() else postOnMain { if (!isDestructed) function() }

    inline fun CSHasDestruct.later(crossinline function: () -> Unit) =
        onMain(after = 0, function)

    inline fun CSHasDestruct.later(after: Int, crossinline function: () -> Unit) =
        onMain(after, function)
}