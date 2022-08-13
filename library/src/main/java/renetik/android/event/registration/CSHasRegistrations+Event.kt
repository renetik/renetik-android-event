package renetik.android.event.registration

import androidx.annotation.UiThread
import renetik.android.core.java.lang.CSThread.currentThread
import renetik.android.core.java.lang.isMain
import renetik.android.core.lang.CSBackground
import renetik.android.core.lang.CSMainHandler.postOnMain
import renetik.android.core.lang.CSMainHandler.removePosted
import renetik.android.core.lang.Func
import renetik.android.event.CSEvent
import renetik.android.event.listen
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import java.util.concurrent.ScheduledFuture

inline fun <T> CSHasRegistrations.listenOnce(
    event: CSEvent<T>, @UiThread crossinline listener: (argument: T) -> Unit) =
    register(event.listen { registration, argument ->
        cancel(registration)
        listener(argument)
    })

fun CSHasRegistrations.background(function: Func): CSRegistration {
    var scheduled: ScheduledFuture<*>? = null
    val registration = register(CSRegistration(isActive = true,
        onCancel = { scheduled?.cancel(false) }))
    scheduled = CSBackground.background {
        if (registration.isActive) function()
        scheduled = null
        cancel(registration)
    }
    return registration
}

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