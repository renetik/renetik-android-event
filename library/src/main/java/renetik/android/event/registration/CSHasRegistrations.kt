package renetik.android.event.registration

import renetik.android.core.java.lang.isMain
import renetik.android.core.lang.CSMainHandler.postOnMain
import renetik.android.core.lang.CSMainHandler.removePosted

interface CSHasRegistrations {
    companion object

    val registrations: CSRegistrations

    // Is here to not call accidentally CSLater
    fun later(delayMilliseconds: Int, function: () -> Unit): CSRegistration {
        val registration = CSFunctionRegistration(function = {
            cancel(it)
            function()
        }, onCancel = { removePosted(it) })
        register(registration)
        postOnMain(if (delayMilliseconds < 10) 10 else delayMilliseconds,
            registration.function)
        return registration
    }

    // Is here to not call accidentally CSLater
    fun later(function: () -> Unit) = later(10, function)

    fun <T : Any> T.onMain(function: (T).() -> Unit): CSRegistration? =
        if (Thread.currentThread().isMain) {
            function()
            null
        } else later { function(this) }
}