package renetik.android.event.registration

import renetik.android.core.java.lang.CSThread.currentThread
import renetik.android.core.java.lang.isMain
import renetik.android.core.lang.CSBackground.backgroundRepeat
import renetik.android.core.lang.CSMainHandler.postOnMain
import renetik.android.core.lang.CSMainHandler.removePosted
import renetik.android.event.common.CSHasRegistrationsHasDestroy
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

fun CSHasRegistrations.later(
    after: Int, function: () -> Unit): CSRegistration {
    val registration = CSFunctionRegistration(function = {
        cancel(it)
        function()
    }, onCancel = { removePosted(it.function) })
    register(registration)
    postOnMain(if (after < 10) 10 else after, registration.function)
    return CSRegistration { cancel(registration) }
}

fun CSHasRegistrations.laterEach(
    interval: Int, after: Int = interval,
    function: (CSRegistration) -> Unit): CSRegistration {
    lateinit var functionRegistration: CSFunctionRegistration
    val outerRegistration = CSRegistration { cancel(functionRegistration) }
    functionRegistration = register(CSFunctionRegistration(function = {
        function(outerRegistration)
        postOnMain(interval, it.function)
    }, onCancel = { removePosted(it.function) }))
    postOnMain(after, functionRegistration.function)
    return outerRegistration
}

fun CSHasRegistrations.later(function: () -> Unit) = later(0, function)

fun <T : CSHasRegistrations> T.onMain(function: (T).() -> Unit): CSRegistration? =
    if (currentThread.isMain) {
        function()
        null
    } else later { function(this) }

