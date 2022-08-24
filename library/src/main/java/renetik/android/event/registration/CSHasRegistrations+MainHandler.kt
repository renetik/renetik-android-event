package renetik.android.event.registration

import renetik.android.core.java.lang.CSThread.currentThread
import renetik.android.core.java.lang.isMain
import renetik.android.core.lang.CSMainHandler.postOnMain
import renetik.android.core.lang.CSMainHandler.removePosted

fun CSHasRegistrations.later(
    delay: Int, function: () -> Unit): CSRegistration {
    val registration = CSFunctionRegistration(function = {
        cancel(it)
        function()
    }, onCancel = { removePosted(it.function) })
    register(registration)
    postOnMain(if (delay < 10) 10 else delay, registration.function)
    return registration
}

fun CSHasRegistrations.laterEach(
    interval: Int, delay: Int = interval, function: () -> Unit): CSRegistration {
    val registration = CSFunctionRegistration(function = {
        function()
        postOnMain(interval, it.function)
    }, onCancel = { removePosted(it.function) })
    register(registration)
    postOnMain(delay, registration.function)
    return registration
}

fun CSHasRegistrations.later(function: () -> Unit) = later(10, function)

fun <T : CSHasRegistrations> T.onMain(function: (T).() -> Unit): CSRegistration? =
    if (currentThread.isMain) {
        function()
        null
    } else later { function(this) }

