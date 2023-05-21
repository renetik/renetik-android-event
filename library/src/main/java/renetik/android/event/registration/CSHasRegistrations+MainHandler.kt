package renetik.android.event.registration

import androidx.annotation.AnyThread
import renetik.android.core.java.lang.CSThread.currentThread
import renetik.android.core.java.lang.isMain
import renetik.android.core.lang.CSMainHandler.mainHandler
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import renetik.android.event.registration.task.CSBackground.background

@AnyThread
fun CSHasRegistrations.registerLater(
    delay: Int = 0, function: () -> Unit,
): CSRegistration {
    lateinit var registration: CSRegistration
    registration = register(later(if (delay < 10) 10 else delay) {
        function()
        cancel(registration)
    })
    return CSRegistration { cancel(registration) }
}

@AnyThread
fun CSHasRegistrations.registerLater(
    function: () -> Unit,
): CSRegistration {
    lateinit var registration: CSRegistration
    registration = register(later(10) {
        function()
        cancel(registration)
    })
    return CSRegistration {
        if (!registration.isCanceled) cancel(registration)
    }
}

@AnyThread
fun CSHasRegistrations.registerLaterEach(
    delay: Int, period: Int = delay, function: () -> Unit,
): CSRegistration {
    val registration = register(mainHandler.laterEach(delay, period, function))
    return CSRegistration { cancel(registration) }
}

@AnyThread
fun <T : CSHasRegistrations> T.registerOnMain(
    function: (T).() -> Unit,
): CSRegistration? = if (currentThread.isMain) {
    function()
    null
} else registerLater { function(this) }

@AnyThread
fun CSHasRegistrations.registerBackground(
    delay: Int, function: () -> Unit,
): CSRegistration {
    lateinit var registration: CSRegistration
    registration = register(background(if (delay < 10) 10 else delay) {
        function()
        cancel(registration)
    })
    return CSRegistration { cancel(registration) }
}

