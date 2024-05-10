package renetik.android.event.common

import renetik.android.core.lang.Func
import renetik.android.event.listen
import renetik.android.event.registration.CSRegistration

fun <T : CSHasDestruct> T.destruct() = onDestruct()

inline fun CSHasDestruct.onDestructed(crossinline func: Func): CSRegistration =
    eventDestruct.listen(func)
