package renetik.android.event.common

import renetik.android.event.CSEvent
import renetik.android.event.delegate
import renetik.android.event.registration.CSHasChangeValue

// TODO: should use just property isDestructed:CSHasChangeValue<Boolean>
interface CSHasDestruct {
    val isDestructed: Boolean
    val eventDestruct: CSEvent<Unit>
    fun onDestruct()
}

fun CSHasDestruct.isDestruct(): CSHasChangeValue<Boolean> =
    eventDestruct.delegate { isDestructed }