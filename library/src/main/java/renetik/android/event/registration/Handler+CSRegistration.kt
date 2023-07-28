package renetik.android.event.registration

import android.os.Handler
import android.os.SystemClock.uptimeMillis
import renetik.android.core.lang.Func
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

/**
 * LeakCanary was reporting false positives for leaks because removeCallbacks
 * looks like doesn't remove runnable immediately
 */
inline fun Handler.laterEach(
    after: Int, period: Int = after, crossinline function: Func
): CSRegistration {
    val token = object {}
    lateinit var registration: CSRegistration
    lateinit var runnable: Func
    runnable = {
        if (!registration.isCanceled) {
            if (registration.isActive) function()
            if (!registration.isCanceled) postAtTime(
                runnable, token, uptimeMillis() + period.toLong()
            )
        }
    }
    postAtTime(runnable, token, uptimeMillis() + after.toLong())
    registration = CSRegistration(isActive = true) { removeCallbacksAndMessages(token) }
    return registration
}

inline fun Handler.later(after: Int, crossinline function: Func): CSRegistration {
    val token = object {}
    var isCanceled = false
    postAtTime({ if (!isCanceled) function() }, token, uptimeMillis() + after.toLong())
    return CSRegistration(isActive = true) {
        isCanceled = true
        removeCallbacksAndMessages(token)
    }
}

inline fun Handler.later(crossinline function: Func): CSRegistration {
    val token = object {}
    var isCanceled = false
    postAtTime({ if (!isCanceled) function() }, token, uptimeMillis())
    return CSRegistration(isActive = true) {
        isCanceled = true
        removeCallbacksAndMessages(token)
    }
}