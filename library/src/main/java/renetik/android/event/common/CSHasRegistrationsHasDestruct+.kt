package renetik.android.event.common

import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.listenOnce

fun <T> T.registerParent(parent: CSHasDestruct)
    where  T : CSHasRegistrations, T : CSHasDestruct = apply {
    listenOnce(parent.eventDestruct) { if (!isDestructed) destruct() }
}
