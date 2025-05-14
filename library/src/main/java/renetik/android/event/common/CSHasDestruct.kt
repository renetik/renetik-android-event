package renetik.android.event.common

import renetik.android.event.CSEvent

// TODO: should use just property isDestructed:CSHasChangeValue<Boolean>
interface CSHasDestruct {
    val isDestructed: Boolean
    val eventDestruct: CSEvent<Unit>
    fun onDestruct()
}