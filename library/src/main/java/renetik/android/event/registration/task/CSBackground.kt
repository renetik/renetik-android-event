package renetik.android.event.registration.task

import android.os.Handler
import android.os.HandlerThread
import androidx.annotation.WorkerThread
import renetik.android.core.java.util.concurrent.background
import renetik.android.core.java.util.concurrent.backgroundRepeat
import renetik.android.core.java.util.concurrent.cancelNotInterrupt
import renetik.android.core.java.util.concurrent.shutdownAndWait
import renetik.android.event.common.CSHasDestruct
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import java.util.concurrent.Executors.newScheduledThreadPool
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture

object CSBackground {

    val handler: Handler by lazy {
        HandlerThread("CSBackground handler").run { start(); Handler(looper) }
    }

    var executor: ScheduledExecutorService = newScheduledThreadPool(3)
        private set

    fun shutdownBackground() = executor.shutdownAndWait()

    val isBackgroundOff get() = executor.isShutdown

    fun restart() {
        shutdownBackground()
        executor = newScheduledThreadPool(3)
//        executor = newSingleThreadScheduledExecutor()
    }

    inline fun background(
        after: Int = 0, @WorkerThread crossinline function: (CSRegistration) -> Unit,
    ): CSRegistration {
        lateinit var task: ScheduledFuture<*>
        val registration = CSRegistration(isActive = true) { task.cancelNotInterrupt() }
        task = executor.background(after.toLong()) {
            if (registration.isActive) function(registration)
        }
        return CSRegistration(isActive = true) { task.cancelNotInterrupt() }
    }

    inline fun backgroundRepeat(
        interval: Int, delay: Int = interval, @WorkerThread crossinline function: () -> Unit,
    ): ScheduledFuture<*> =
        executor.backgroundRepeat(delay.toLong(), interval.toLong(), function)

    inline fun backgroundRepeat(
        interval: Int, @WorkerThread crossinline function: () -> Unit,
    ): ScheduledFuture<*> =
        executor.backgroundRepeat(interval.toLong(), function)

    inline fun CSHasDestruct.background(crossinline function: () -> Unit) {
        background(after = 0, function)
    }

    inline fun CSHasDestruct.background(after: Int = 0, crossinline function: () -> Unit) {
        executor.background(after.toLong()) { if (!isDestructed) function() }
    }

    inline fun CSHasDestruct.backgroundRepeat(
        interval: Int, after: Int = interval,
        crossinline function: (CSRegistration) -> Unit,
    ): CSRegistration {
        lateinit var registration: CSRegistration
        val task = executor.backgroundRepeat(interval.toLong(), after.toLong()) {
            if (!isDestructed) function(registration)
        }
        registration = CSRegistration { task.cancel(true) }
        return registration
    }
}

