package renetik.android.event.owner

import renetik.android.core.java.lang.isMain
import renetik.android.core.lang.CSMainHandler.postOnMain
import renetik.android.core.lang.CSMainHandler.removePosted
import renetik.android.event.registration.CSFunctionRegistration
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.CSRegistrations

interface CSEventOwner {
    val registrations: CSRegistrations

    // Is here to not call accidentally CSLater
    fun later(delayMilliseconds: Int, function: () -> Unit): CSRegistration {
        val registration = CSFunctionRegistration(function = {
            function()
            remove(it)
        }, onCancel = ::removePosted)
        postOnMain(delayMilliseconds, registration.function)
        return registration
    }

    // Is here to not call accidentally CSLater
    fun later(function: () -> Unit) = later(0, function)

    fun <T : Any> T.onMain(function: (T).() -> Unit): CSRegistration? =
        if (Thread.currentThread().isMain) {
            function()
            null
        } else later(5) { function(this) }
}