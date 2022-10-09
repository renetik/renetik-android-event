package renetik.android.event.common

import renetik.android.event.listenOnce

fun CSHasDestroy.destroy() = onDestroy()

//fun CSHasDestroy.onDestroy(listener: () -> Unit) =
//    eventDestroy.listenOnce { listener() }