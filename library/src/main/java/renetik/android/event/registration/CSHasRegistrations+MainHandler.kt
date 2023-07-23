package renetik.android.event.registration

import androidx.annotation.AnyThread
import renetik.android.core.lang.CSHandler.mainHandler
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import renetik.android.event.registration.task.CSBackground.background
import renetik.android.event.util.CSLater.later

@AnyThread
fun CSHasRegistrations.registerLater(
    delay: Int = 0, function: () -> Unit,
): CSRegistration {
    lateinit var registration: CSRegistration
    registration = register(later(if (delay < 10) 10 else delay) {
        cancel(registration)
        function()
    })
    return CSRegistration { if (!registration.isCanceled) cancel(registration) }
}

@Deprecated("Can we use instead later extension on HasDestroy ?")
@AnyThread
fun CSHasRegistrations.registerLater(
    function: () -> Unit,
): CSRegistration {
    lateinit var registration: CSRegistration
    registration = register(later(10) {
        cancel(registration)
        function()
    })
    return CSRegistration { if (!registration.isCanceled) cancel(registration) }
}

@AnyThread
fun CSHasRegistrations.registerLaterEach(
    delay: Int, period: Int = delay, function: () -> Unit,
): CSRegistration {
    val registration = register(mainHandler.laterEach(delay, period, function))
    return CSRegistration { cancel(registration) }
}

//@AnyThread
//fun <T : CSHasRegistrations> T.registerOnMain(
//    function: (T).() -> Unit,
//): CSRegistration? = if (currentThread.isMain) {
//    function()
//    null
//} else registerLater { function(this) }

@AnyThread
fun CSHasRegistrations.registerBackground(
    delay: Int, function: () -> Unit,
): CSRegistration {
    lateinit var registration: CSRegistration
    registration = register(background(if (delay < 10) 10 else delay) {
        function()
        cancel(registration)
    })
    return CSRegistration { if (!registration.isCanceled) cancel(registration) }
}

