package renetik.android.event.registration

import renetik.android.core.java.lang.CSThread.currentThread
import renetik.android.core.java.lang.isMain
import renetik.android.core.lang.CSMainHandler.mainHandler
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

fun CSHasRegistrations.registerLater(
    after: Int, function: () -> Unit
): CSRegistration {
    val registration = register(mainHandler.registerLater(if (after < 10) 10 else after, function))
    return CSRegistration { cancel(registration) }
}

fun CSHasRegistrations.registerRepeat(
    interval: Int, after: Int = interval, function: () -> Unit
): CSRegistration {
    val registration = register(mainHandler.repeat(interval, after, function))
    return CSRegistration { cancel(registration) }
}

fun CSHasRegistrations.registerLater(function: () -> Unit) = registerLater(5, function)

fun <T : CSHasRegistrations> T.registerOnMain(function: (T).() -> Unit): CSRegistration? =
    if (currentThread.isMain) {
        function()
        null
    } else registerLater { function(this) }

