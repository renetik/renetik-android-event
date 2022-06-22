package renetik.android.event.owner

import renetik.android.event.CSEvent
import renetik.android.event.listenOnce

interface CSHasDestroy {
    val eventDestroy: CSEvent<Unit>
    fun onDestroy()
}

fun CSHasDestroy.onDestroy(listener: () -> Unit) = eventDestroy.listenOnce { listener() }