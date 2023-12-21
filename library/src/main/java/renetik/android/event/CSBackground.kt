package renetik.android.event

import androidx.annotation.WorkerThread
import renetik.android.core.java.util.concurrent.background
import renetik.android.core.java.util.concurrent.backgroundEach
import renetik.android.core.java.util.concurrent.cancelNotInterrupt
import renetik.android.core.java.util.concurrent.shutdownAndWait
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import java.util.concurrent.Executors.defaultThreadFactory
import java.util.concurrent.Executors.newScheduledThreadPool
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import kotlin.math.max
import kotlin.properties.Delegates.notNull
import kotlin.time.Duration

object CSBackground {

    var executor: ScheduledExecutorService = createExecutor()
        private set

    fun shutdown() = executor.shutdownAndWait()

    val isOff get() = executor.isShutdown

    fun restart() {
        shutdown()
        executor = createExecutor()
    }

    private fun createExecutor(): ScheduledExecutorService = newScheduledThreadPool(3) {
        defaultThreadFactory().newThread(it).apply { name = "CSBackground-$name" }
    }

    inline fun background(
        after: Duration, @WorkerThread crossinline function: (CSRegistration) -> Unit
    ): CSRegistration = background(after.inWholeMilliseconds.toInt(), function)

    inline fun background(
        after: Int = 0, @WorkerThread crossinline function: (CSRegistration) -> Unit,
    ): CSRegistration {
        var task: ScheduledFuture<*> by notNull()
        val registration = CSRegistration(isActive = true) { task.cancelNotInterrupt() }
        task = executor.background(max(after.toLong(), 1)) {
            if (registration.isActive) function(registration)
        }
        return registration
    }

    inline fun backgroundEach(
        after: Int, period: Int = after,
        crossinline function: (CSRegistration) -> Unit,
    ): CSRegistration {
        var task: ScheduledFuture<*> by notNull()
        val registration = CSRegistration(isActive = true) { task.cancelNotInterrupt() }
        task = executor.backgroundEach(period.toLong(), max(after.toLong(), 1)) {
            if (registration.isActive) function(registration)
        }
        return registration
    }
}
