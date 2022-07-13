package renetik.android.event.registration

import renetik.android.core.java.lang.isMain
import renetik.android.core.kotlin.unsupported
import renetik.android.core.lang.CSMainHandler.postOnMain
import renetik.android.core.lang.CSMainHandler.removePosted

interface CSHasRegistrations {
    companion object

    val registrations: CSRegistrations

    // Is here to not call accidentally CSLater
    fun later(delayMilliseconds: Int, function: () -> Unit): CSRegistration {
        if (delayMilliseconds == 0) unsupported("delay have to be > 0")
        val registration = CSFunctionRegistration(function = {
            function()
            remove(it)
        }, onCancel = ::removePosted)
        postOnMain(delayMilliseconds, registration.function)
        return registration
    }

    // Is here to not call accidentally CSLater
    fun later(function: () -> Unit) = later(5, function)

    fun <T : Any> T.onMain(function: (T).() -> Unit): CSRegistration? =
        if (Thread.currentThread().isMain) {
            function()
            null
        } else later(5) { function(this) }
}