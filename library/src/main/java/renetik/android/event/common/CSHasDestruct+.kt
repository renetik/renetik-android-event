package renetik.android.event.common

import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import renetik.android.event.registration.task.CSBackground.background
import renetik.android.event.registration.task.CSBackground.backgroundRepeat

fun CSHasDestruct.destruct() = onDestruct()

fun CSHasDestruct.backgroundEach(
    interval: Int, after: Int = interval,
    function: (CSRegistration) -> Unit,
): CSRegistration {
    lateinit var registration: CSRegistration
    val task = backgroundRepeat(after.toLong(), interval.toLong()) {
        if (!isDestructed) function(registration)
    }
    registration = CSRegistration { task.cancel(true) }
    return registration
}