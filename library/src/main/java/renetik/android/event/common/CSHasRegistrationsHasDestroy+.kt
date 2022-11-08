package renetik.android.event.common

import renetik.android.core.lang.CSBackground
import renetik.android.core.lang.CSBackground.backgroundRepeat
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import renetik.android.event.registration.listenOnce
import renetik.android.event.registration.register

fun <T> T.parent(parent: CSHasDestruct)
        where  T : CSHasRegistrations, T : CSHasDestruct = apply {
    listenOnce(parent.eventDestruct) { destruct() }
}

fun CSHasRegistrationsHasDestroy.backgroundEach(
    interval: Int, after: Int = interval,
    function: (CSRegistration) -> Unit): CSRegistration {
    lateinit var registration: CSRegistration
    val task = backgroundRepeat(after.toLong(), interval.toLong()) {
        if (!isDestructed) function(registration)
    }
    registration = register(CSRegistration { task.cancel(true) })
    return registration
}