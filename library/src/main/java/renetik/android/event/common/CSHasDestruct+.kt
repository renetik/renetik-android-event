package renetik.android.event.common

import renetik.android.core.lang.CSBackground.backgroundRepeat
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

fun CSHasDestruct.destruct() = onDestruct()

fun CSHasDestruct.backgroundEach(
    interval: Int, after: Int = interval,
    function: (CSRegistration) -> Unit
): CSRegistration {
    lateinit var registration: CSRegistration
    val task = backgroundRepeat(after.toLong(), interval.toLong()) {
        if (!isDestructed) function(registration)
    }
    registration = CSRegistration { task.cancel(true) }
    return registration
}
