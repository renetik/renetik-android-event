package renetik.android.event.common

import renetik.android.core.java.lang.isThreadMain
import renetik.android.core.lang.CSMainHandler.postOnMain

fun CSHasDestruct.onMain(after: Int, function: () -> Unit) =
    postOnMain(after) { if (!isDestructed) function() }

fun CSHasDestruct.onMain(function: () -> Unit) =
    if (isThreadMain) function() else postOnMain { if (!isDestructed) function() }