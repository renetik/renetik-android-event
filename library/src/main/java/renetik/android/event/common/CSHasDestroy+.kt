package renetik.android.event.common

import renetik.android.event.listenOnce

fun CSHasDestroy.onDestroy(listener: () -> Unit) = eventDestroy.listenOnce { listener() }

fun CSHasDestroy.destroy() = onDestroy()