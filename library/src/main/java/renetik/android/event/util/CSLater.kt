package renetik.android.event.util

import renetik.android.core.lang.CSMainHandler.postOnMain
import renetik.android.core.lang.CSMainHandler.removePosted
import renetik.android.event.registration.CSFunctionRegistration
import renetik.android.event.registration.CSRegistration

object CSLater {
    fun later(delayMilliseconds: Int, function: () -> Unit): CSRegistration {
        val registration = CSFunctionRegistration(function = { function() },
            onCancel = { removePosted(it.function) })
        val delay = if (delayMilliseconds < 5) 5 else delayMilliseconds
        postOnMain(delay, registration.function)
        return registration
    }

    fun later(function: () -> Unit) = later(5, function)
}