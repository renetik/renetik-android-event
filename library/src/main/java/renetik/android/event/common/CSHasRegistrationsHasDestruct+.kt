package renetik.android.event.common

import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.listenOnce

fun <T> T.parent(parent: CSHasDestruct)
    where  T : CSHasRegistrations, T : CSHasDestruct = apply {
    listenOnce(parent.eventDestruct) { destruct() }
}
