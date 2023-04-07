package renetik.android.event.registration

import renetik.android.core.java.lang.CSThread.currentThread
import renetik.android.core.java.lang.isMain
import renetik.android.core.lang.CSMainHandler.mainHandler
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

fun CSHasRegistrations.registerLater(
    delay: Int, function: () -> Unit
): CSRegistration {
    val registration = register(mainHandler.registerLater(if (delay < 10) 10 else delay, function))
    return CSRegistration { cancel(registration) }
}

fun CSHasRegistrations.registerRepeat(
    delay: Int, period: Int = delay, function: () -> Unit
): CSRegistration {
    val registration = register(mainHandler.repeat(delay, period, function))
    return CSRegistration { cancel(registration) }
}

fun CSHasRegistrations.registerLater(function: () -> Unit) = registerLater(5, function)

fun <T : CSHasRegistrations> T.registerOnMain(function: (T).() -> Unit): CSRegistration? =
    if (currentThread.isMain) {
        function()
        null
    } else registerLater { function(this) }

