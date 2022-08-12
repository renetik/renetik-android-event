package renetik.android.event.util

import renetik.android.core.java.lang.CSThread.currentThread
import renetik.android.core.java.lang.isMain
import renetik.android.core.kotlin.unsupported
import renetik.android.core.lang.CSMainHandler.postOnMain
import renetik.android.core.lang.CSMainHandler.removePosted
import renetik.android.event.registration.CSFunctionRegistration
import renetik.android.event.registration.CSRegistration

object CSLater {
    fun later(delayMilliseconds: Int, function: () -> Unit): CSRegistration {
        if (delayMilliseconds == 0) unsupported("delay have to be > 0")
        val registration = CSFunctionRegistration(function = { function() },
            onCancel = { removePosted(it.function) })
        postOnMain(delayMilliseconds, registration.function)
        return registration
    }

    fun later(function: () -> Unit) = later(5, function)

    fun <T : Any> T.onMain(function: (T).() -> Unit): CSRegistration? =
        if (currentThread.isMain) {
            function()
            null
        } else later { function(this) }
}