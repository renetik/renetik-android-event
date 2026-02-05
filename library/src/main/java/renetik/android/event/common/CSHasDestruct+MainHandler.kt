package renetik.android.event.common

import renetik.android.core.java.lang.isThreadMain
import renetik.android.core.lang.CSHandler.mainHandler
import renetik.android.core.lang.send

inline fun CSHasDestruct.later(crossinline function: () -> Unit) =
    mainHandler.send { if (!isDestructed) function() }

inline fun CSHasDestruct.onMain(crossinline function: () -> Unit) =
    if (isThreadMain) function() else later { function() }