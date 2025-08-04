package renetik.android.event.common

import renetik.android.event.CSEvent

interface CSHasDestruct {
    val isDestructed: Boolean
    val eventDestruct: CSEvent<Unit>
    fun onDestruct() = Unit
}