package renetik.android.event.registration

import renetik.android.core.java.lang.CSThread.currentThread
import renetik.android.core.java.lang.isMain
import renetik.android.core.lang.CSMainHandler.postOnMain
import renetik.android.core.lang.CSMainHandler.removePosted
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

fun CSHasRegistrations.later(
    delay: Int, function: () -> Unit): CSRegistration {
    val registration = CSFunctionRegistration(function = {
        cancel(it)
        function()
    }, onCancel = { removePosted(it.function) })
    register(registration)
    postOnMain(if (delay < 10) 10 else delay, registration.function)
    return CSRegistration { cancel(registration) }
}

fun CSHasRegistrations.laterEach(
    interval: Int, delay: Int = interval,
    function: (CSRegistration) -> Unit): CSRegistration {
    lateinit var functionRegistration: CSFunctionRegistration
    val outerRegistration = CSRegistration { cancel(functionRegistration) }
    functionRegistration = register(CSFunctionRegistration(function = {
        function(outerRegistration)
        postOnMain(interval, it.function)
    }, onCancel = { removePosted(it.function) }))
    postOnMain(delay, functionRegistration.function)
    return outerRegistration
}

fun CSHasRegistrations.later(function: () -> Unit) = later(0, function)

fun <T : CSHasRegistrations> T.onMain(function: (T).() -> Unit): CSRegistration? =
    if (currentThread.isMain) {
        function()
        null
    } else later { function(this) }

