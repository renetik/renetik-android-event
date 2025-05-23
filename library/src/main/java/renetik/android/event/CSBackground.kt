package renetik.android.event

import androidx.annotation.WorkerThread
import renetik.android.core.java.util.concurrent.background
import renetik.android.core.java.util.concurrent.backgroundEach
import renetik.android.core.java.util.concurrent.cancelInterrupt
import renetik.android.core.java.util.concurrent.shutdownAndWait
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import renetik.android.event.registration.start
import java.util.concurrent.Executors.defaultThreadFactory
import java.util.concurrent.Executors.newScheduledThreadPool
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import kotlin.math.max
import kotlin.properties.Delegates.notNull
import kotlin.time.Duration

object CSBackground {

    // has to be public for inline functions
    var executor: ScheduledExecutorService = createExecutor()
        private set

//    var dispatcher: ExecutorCoroutineDispatcher = executor.asCoroutineDispatcher()
//        private set

//    var scope: CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)
//        private set

    fun shutdown() = executor.shutdownAndWait()

    val isOff get() = executor.isShutdown

    fun restart() {
//        scope.cancel()
//        dispatcher.close()
        shutdown()
        executor = createExecutor()
//        dispatcher = executor.asCoroutineDispatcher()
//        scope = CoroutineScope(SupervisorJob() + dispatcher)
    }

    private fun createExecutor(): ScheduledExecutorService = newScheduledThreadPool(3) {
        defaultThreadFactory().newThread(it).apply { name = "CSBackground-$name" }
    }

//    fun launch(func: suspend (JobRegistration) -> Unit): JobRegistration =
//        scope.start(func)

    inline fun background(
        after: Duration, @WorkerThread crossinline function: (CSRegistration) -> Unit,
    ): CSRegistration = background(after.inWholeMilliseconds.toInt(), function)

    inline fun background(
        after: Int = 0, @WorkerThread crossinline function: (CSRegistration) -> Unit,
    ): CSRegistration {
        var task: ScheduledFuture<*> by notNull()
        val registration = CSRegistration(isActive = true) { task.cancelInterrupt() }
        task = executor.background(max(after.toLong(), 1)) {
            if (registration.isActive) function(registration)
        }
        return registration
    }


    inline fun backgroundEach(
        after: Int, period: Int = after, start: Boolean = true,
        @WorkerThread crossinline function: (CSRegistration) -> Unit,
    ): CSRegistration {
        var task: ScheduledFuture<*> by notNull()
        var registration: CSRegistration by notNull()
        registration = CSRegistration(
            onResume = {
                task = executor.backgroundEach(period.toLong(), max(after.toLong(), 1)) {
                    if (registration.isActive) function(registration)
                }
            }, onPause = { task.cancelInterrupt() }
        ).apply { if (start) start() }
        return registration
    }
}