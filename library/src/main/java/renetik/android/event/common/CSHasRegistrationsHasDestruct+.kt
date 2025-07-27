package renetik.android.event.common

import renetik.android.event.listenOnce
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.plus

fun <T> T.parent(parent: CSHasDestruct)
        where  T : CSHasRegistrations, T : CSHasDestruct = apply {
    this + parent.eventDestruct.listenOnce { if (!isDestructed) destruct() }
}
