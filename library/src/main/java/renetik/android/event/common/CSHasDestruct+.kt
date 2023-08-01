package renetik.android.event.common

import renetik.android.core.lang.Func
import renetik.android.event.listen

fun CSHasDestruct.destruct() = onDestruct()

inline fun CSHasDestruct.onDestructed(crossinline func: Func) = eventDestruct.listen(func)
