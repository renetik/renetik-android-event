package renetik.android.event.common

import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.registerListenOnce

fun <T> T.registerParent(parent: CSHasDestruct)
    where  T : CSHasRegistrations, T : CSHasDestruct = apply {
    registerListenOnce(parent.eventDestruct) { if (!isDestructed) destruct() }
}
