package renetik.android.event.dispatch

import renetik.android.event.lifecycle.*

import renetik.android.event.registration.*
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

import androidx.annotation.MainThread
import renetik.android.core.android.os.isThreadMain
import renetik.android.core.android.os.CSHandler.mainHandler
import renetik.android.core.android.os.send

inline fun CSHasDestruct.later(@MainThread crossinline function: () -> Unit) =
    mainHandler.send { if (!isDestructed) function() }

inline fun CSHasDestruct.onMain(@MainThread crossinline function: () -> Unit) =
    if (isThreadMain) function() else later { function() }