package renetik.android.event.common

import renetik.android.core.java.lang.isThreadMain
import renetik.android.core.lang.CSMainHandler

/**
 * Lightweight solution without event registration to prevent invocation after isDestructed
 */
fun CSHasDestruct.postOnMain(function: () -> Unit) =
    CSMainHandler.postOnMain { if (!isDestructed) function() }

fun CSHasDestruct.postOnMain(delay: Int, function: () -> Unit) =
    CSMainHandler.postOnMain(delay) { if (!isDestructed) function() }

fun CSHasDestruct.onMain(function: () -> Unit) =
    if (isThreadMain) function() else postOnMain { if (!isDestructed) function() }