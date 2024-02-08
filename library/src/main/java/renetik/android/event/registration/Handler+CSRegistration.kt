package renetik.android.event.registration

import android.os.Handler
import android.os.SystemClock.uptimeMillis
import renetik.android.core.lang.Func
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import kotlin.time.Duration

/**
 * LeakCanary was reporting false positives for leaks because removeCallbacks
 * looks like doesn't remove runnable immediately
 */
inline fun Handler.laterEach(
    after: Int, period: Int = after, crossinline function: Func,
): CSRegistration {
    val token = object {}
    lateinit var registration: CSRegistration
    lateinit var runnable: Func
    runnable = {
        if (registration.isActive) {
            function()
            postAtTime(runnable, token, uptimeMillis() + period.toLong())
        }
    }
    registration = CSRegistration(
        onResume = { postAtTime(runnable, token, uptimeMillis() + after.toLong()) },
        onCancel = { removeCallbacksAndMessages(token) }
    ).start()
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

inline fun Handler.laterEach(
    after: Duration, period: Duration = after, crossinline function: Func,
) = laterEach(after.inWholeMilliseconds.toInt(), period.inWholeMilliseconds.toInt(), function)

inline fun Handler.later(after: Duration, crossinline function: Func) =
    later(after.inWholeMilliseconds.toInt(), function)