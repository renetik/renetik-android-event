package renetik.android.event.common

import renetik.android.core.java.lang.isThreadMain
import renetik.android.core.lang.CSHandler.postOnMain
import renetik.android.event.registration.task.CSBackground

inline fun CSHasDestruct.onMain(after: Int, crossinline function: () -> Unit) =
    postOnMain(after) { if (!isDestructed) function() }

inline fun CSHasDestruct.onMain(crossinline function: () -> Unit) =
    if (isThreadMain) function() else postOnMain { if (!isDestructed) function() }

inline fun CSHasDestruct.later(crossinline function: () -> Unit) =
    onMain(after = 0, function)

inline fun CSHasDestruct.later(after: Int, crossinline function: () -> Unit) =
    onMain(after, function)

//inline fun CSHasDestruct.background(crossinline function: () -> Unit) {
//    CSBackground.background(after = 0) { if (!isDestructed) function() }
//}
//
//inline fun CSHasDestruct.background(after: Int, crossinline function: () -> Unit) {
//    CSBackground.background(after) { if (!isDestructed) function() }
//}
