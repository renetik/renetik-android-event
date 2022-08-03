package renetik.android.event.registration

import androidx.annotation.UiThread
import renetik.android.core.lang.CSBackground
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