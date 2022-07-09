package renetik.android.event.util

import renetik.android.core.lang.CSMainHandler.postOnMain
import renetik.android.core.lang.CSMainHandler.removePosted
import renetik.android.event.registration.CSFunctionRegistration
import renetik.android.event.registration.CSRegistration

object CSLater {

    fun later(delayMilliseconds: Int, function: () -> Unit): CSRegistration {
        val registration = CSFunctionRegistration(function = { function() },
            onCancel = ::removePosted)
        postOnMain(delayMilliseconds, registration.function)
        return registration
    }

    fun later(function: () -> Unit) = later(0, function)
}