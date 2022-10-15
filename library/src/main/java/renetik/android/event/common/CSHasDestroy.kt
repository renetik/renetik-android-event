package renetik.android.event.common

import renetik.android.event.CSEvent

interface CSHasDestroy {
    val isDestroyed: Boolean
    val eventDestroy: CSEvent<Unit>
    fun onDestroy()
}