package renetik.android.event.registration.task

import androidx.annotation.WorkerThread
import renetik.android.core.java.util.concurrent.background
import renetik.android.core.java.util.concurrent.backgroundNano
import renetik.android.core.java.util.concurrent.backgroundRepeat
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor

object CSBackground {
    val executor = ScheduledThreadPoolExecutor(3)

    inline fun background(
        after: Int = 0,
        @WorkerThread crossinline function: (CSRegistration) -> Unit,
    ): CSRegistration {
        lateinit var task: ScheduledFuture<*>
        val registration = CSRegistration(isActive = true) { task.cancel(true) }
        task = executor.background(after.toLong()) {
            if (registration.isActive) function(registration)
        }
        return registration
    }

    inline fun backgroundNano(
        delay: Long = 0, @WorkerThread crossinline function: () -> Unit,
    ): ScheduledFuture<*> =
        executor.backgroundNano(delay = delay, function = function)

    inline fun backgroundRepeat(
        delay: Long, period: Long, @WorkerThread crossinline function: () -> Unit,
    ): ScheduledFuture<*> =
        executor.backgroundRepeat(delay = delay, period = period, function = function)

    inline fun backgroundRepeat(
        period: Long, @WorkerThread crossinline function: () -> Unit,
    ): ScheduledFuture<*> =
        executor.backgroundRepeat(period, function)
}

