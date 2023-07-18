package renetik.android.event.registration

import android.os.Handler
import android.os.SystemClock.uptimeMillis
import renetik.android.core.lang.CSHandler.mainHandler
import renetik.android.core.lang.Func
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

/**
 * LeakCanary was reporting false positives for leaks because removeCallbacks
 * looks like doesn't remove runnable immediately
 */
fun Handler.laterEach(
    after: Int, period: Int = after, function: Func
): CSRegistration {
    val token = object {}
    var isCanceled = false
    lateinit var runnable: Func
    runnable = {
        if (!isCanceled) {
            function()
            if (!isCanceled) postAtTime(runnable, token, uptimeMillis() + period.toLong())
        }
    }
    postAtTime(runnable, token, uptimeMillis() + after.toLong())
    return CSRegistration(isActive = true) {
        isCanceled = true
        removeCallbacksAndMessages(token)
    }
}

// Worked good:
//fun Handler.repeat(
//    delay: Int, period: Int = delay, function: Func
//): CSRegistration {
//    val token = object {}
//    var isCanceled = false
//    var weakRunnable: Func? by weak()
//    val runnable = {
//        if (!isCanceled) {
//            function()
//            if (!isCanceled){
////                weakRunnable?.let { postAtTime(it, period.toLong()) }
//                weakRunnable?.let { postAtTime(it, token, uptimeMillis() + period.toLong()) }
//            }
//        }
//    }
//    weakRunnable = runnable
////    weakRunnable?.let { postDelayed(it, delay.toLong()) }
//    weakRunnable?.let { postAtTime(it, token, uptimeMillis() + delay.toLong()) }
//    return CSRegistration {
//        isCanceled = true
////        weakRunnable?.let { removeCallbacks(it) }
//        removeCallbacksAndMessages(token)
//    }
//}

//TODO: Why we have this another special later ?
fun Handler.later(
    after: Int, function: Func
): CSRegistration {
    val token = object {}
    var isCanceled = false
    postAtTime({ if (!isCanceled) function() }, token, uptimeMillis() + after.toLong())
    return CSRegistration(isActive = true) {
        isCanceled = true
        removeCallbacksAndMessages(token)
    }
}

//fun Handler.later(
//    after: Int, function: Func
//): CSRegistration {
//    var isCanceled = false
//    val runnable: Func = { if (!isCanceled) function() }
//    val weakRunnable: Func? by weak(runnable)
//    postDelayed(weakRunnable!!, after.toLong())
//    return CSRegistration {
//        isCanceled = true
//        removeCallbacks(runnable)
//    }
//}

fun laterEach(
    after: Int, period: Int = after, function: () -> Unit
): CSRegistration = mainHandler.laterEach(after, period, function)

fun later(after: Int, function: () -> Unit): CSRegistration =
    mainHandler.later(after, function)


