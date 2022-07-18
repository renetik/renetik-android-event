package renetik.android.event.common

import renetik.android.event.registration.listenOnce

fun CSHasRegistrationsHasDestroy.onDestroy(listener: () -> Unit) =
    listenOnce(eventDestroy) { listener() }