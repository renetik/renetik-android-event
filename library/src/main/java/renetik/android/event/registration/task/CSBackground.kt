package renetik.android.event.registration.task

import androidx.annotation.WorkerThread
import java.util.concurrent.Executors.defaultThreadFactory
import java.util.concurrent.Executors.newScheduledThreadPool
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import renetik.android.core.java.util.concurrent.background
import renetik.android.core.java.util.concurrent.backgroundEach
import renetik.android.core.java.util.concurrent.cancelNotInterrupt
import renetik.android.core.java.util.concurrent.shutdownAndWait
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

object CSBackground {

    var executor: ScheduledExecutorService = createExecutor()
        private set

    fun shutdownBackground() = executor.shutdownAndWait()

    val isBackgroundOff get() = executor.isShutdown

    fun restart() {
        shutdownBackground()
        executor = createExecutor()
    }

    private fun createExecutor(): ScheduledExecutorService = newScheduledThreadPool(3) {
        defaultThreadFactory().newThread(it).apply { name = "CSBackground-$name" }
    }

    inline fun background(
        after: Int = 0, @WorkerThread crossinline function: (CSRegistration) -> Unit,
    ): CSRegistration {
        var task: ScheduledFuture<*>? = null
        val registration = CSRegistration(isActive = true) { task?.cancelNotInterrupt() }
        task = executor.background(after.toLong()) {
            if (registration.isActive) function(registration)
        }
        if (registration.isCanceled && !task.isCancelled) task.cancel(true)
        return registration
    }

    inline fun backgroundEach(
        after: Int, period: Int = after,
        crossinline function: (CSRegistration) -> Unit,
    ): CSRegistration {
        var task: ScheduledFuture<*>? = null
        val registration = CSRegistration(isActive = true) { task?.cancelNotInterrupt() }
        task = executor.backgroundEach(period.toLong(), after.toLong()) {
            if (registration.isActive) function(registration)
        }
        if (registration.isCanceled && !task.isCancelled) task.cancel(true)
        return registration
    }
}

