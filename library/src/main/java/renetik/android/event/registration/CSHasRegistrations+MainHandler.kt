package renetik.android.event.registration

import androidx.annotation.AnyThread
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import renetik.android.core.lang.CSHandler.mainHandler
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import kotlin.time.Duration

@AnyThread
inline fun CSHasRegistrations.laterEach(
    after: Int, period: Int = after,
    start: Boolean = true, crossinline function: () -> Unit,
): CSRegistration = this + mainHandler.laterEach(after, period, start, function)

@AnyThread
inline fun CSHasRegistrations.laterEach(
    after: Duration, period: Duration = after,
    start: Boolean = true, crossinline function: () -> Unit,
): CSRegistration = this + mainHandler.laterEach(after, period, start, function)

@AnyThread
inline fun CSHasRegistrations.launchRepeat(
    after: Int = 0, period: Int = after, start: Boolean = true,
    crossinline function: suspend () -> Unit,
): CSRegistration = launchRepeat(
    dispatcher = Main, period = period, start = start, function = function
)

@AnyThread
inline fun CSHasRegistrations.launchRepeat(
    dispatcher: CoroutineDispatcher = Main, after: Int = 0,
    period: Int = after, start: Boolean = true,
    crossinline function: suspend () -> Unit,
): CSRegistration {
    val resumeChannel = Channel<Unit>(Channel.CONFLATED)
    val registration = launch(dispatcher) {
        for (signal in resumeChannel) {
            delay(after.toLong())
            while (isActive) {
                function()
                delay(period.toLong())
            }
        }
    }
    return CSRegistration(
        onResume = {
            registration.resume()
            resumeChannel.trySend(Unit)
        },
        onPause = { registration.pause() },
        onCancel = {
            registration.cancel()
            resumeChannel.close()
        }
    ).also {
        if (start) it.start()
    }
}