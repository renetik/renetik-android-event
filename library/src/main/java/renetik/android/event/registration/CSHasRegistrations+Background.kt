package renetik.android.event.registration

import renetik.android.core.lang.CSBackground
import renetik.android.core.lang.Func
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import java.util.concurrent.ScheduledFuture

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