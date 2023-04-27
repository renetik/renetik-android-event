package renetik.android.event.registration

import androidx.annotation.WorkerThread
import renetik.android.core.lang.CSBackground
import renetik.android.core.lang.CSBackground.background
import renetik.android.core.lang.Func
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import java.util.concurrent.ScheduledFuture

fun CSHasRegistrations.registerBackground(@WorkerThread function: Func): CSRegistration {
    var scheduled: ScheduledFuture<*>? = null
    val registration = register(CSRegistration(isActive = true,
        onCancel = { scheduled?.cancel(false) }))
    scheduled = background {
        if (registration.isActive) function()
        scheduled = null
        cancel(registration)
    }
    return registration
}