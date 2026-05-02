package renetik.android.event.common

import androidx.annotation.MainThread
import renetik.android.core.java.lang.isThreadMain
import renetik.android.core.lang.CSHandler.mainHandler
import renetik.android.core.lang.send

inline fun CSHasDestruct.later(@MainThread crossinline function: () -> Unit) =
    mainHandler.send { if (!isDestructed) function() }

inline fun CSHasDestruct.onMain(@MainThread crossinline function: () -> Unit) =
    if (isThreadMain) function() else later { function() }