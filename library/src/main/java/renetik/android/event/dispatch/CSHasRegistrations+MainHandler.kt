package renetik.android.event.dispatch

import renetik.android.event.lifecycle.*

import renetik.android.event.registration.*
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

import renetik.android.core.kotlin.primitives.min
import renetik.android.core.lang.CSHandler.mainHandler
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.CSRegistration
import renetik.android.event.dispatch.later
import renetik.android.event.registration.plus
import kotlin.time.Duration

inline fun CSHasRegistrations.later(
    after: Int, crossinline function: () -> Unit,
): CSRegistration = this + mainHandler.later(after.min(10)) { function() }

inline fun CSHasRegistrations.later(
    after: Duration, crossinline function: () -> Unit,
): CSRegistration = later(after.inWholeMilliseconds.toInt(), function)