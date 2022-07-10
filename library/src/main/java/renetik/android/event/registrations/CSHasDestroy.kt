package renetik.android.event.registrations

import renetik.android.event.CSEvent
import renetik.android.event.listenOnce

interface CSHasDestroy {
	val eventDestroy: CSEvent<Unit>
	fun onDestroy()
}

fun CSHasDestroy.onDestroy(listener: () -> Unit) = eventDestroy.listenOnce { listener() }
fun CSHasDestroy.destroy() = onDestroy()