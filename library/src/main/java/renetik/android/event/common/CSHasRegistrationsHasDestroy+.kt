package renetik.android.event.common

import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.listenOnce

fun <T> T.parent(parent: CSHasDestroy)
        where  T : CSHasRegistrations, T : CSHasDestroy = apply {
    listenOnce(parent.eventDestroy) { onDestroy() }
}