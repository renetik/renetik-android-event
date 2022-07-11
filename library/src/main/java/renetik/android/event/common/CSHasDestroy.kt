package renetik.android.event.common

import renetik.android.event.CSEvent

interface CSHasDestroy {
    val eventDestroy: CSEvent<Unit>
    fun onDestroy()
}