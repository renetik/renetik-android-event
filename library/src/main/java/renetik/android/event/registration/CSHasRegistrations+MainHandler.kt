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
    delay: Int, after: Int = delay,
    start: Boolean = true, crossinline function: () -> Unit,
): CSRegistration = this + mainHandler.laterEach(after, delay, start, function)

@AnyThread
inline fun CSHasRegistrations.laterEach(
    delay: Duration, after: Duration = delay,
    start: Boolean = true, crossinline function: () -> Unit,
): CSRegistration = this + mainHandler.laterEach(after, delay, start, function)

fun CSHasRegistrations.launchWhileActive(
    dispatcher: CoroutineDispatcher = Main,
    func: suspend () -> Unit,
) = launchRepeat(dispatcher, 0, function = func)

@AnyThread
inline fun CSHasRegistrations.launchRepeat(
    delay: Duration, after: Duration = delay, start: Boolean = true,
    crossinline function: suspend () -> Unit,
): CSRegistration = launchRepeat(Main, delay, after, start, function)

@AnyThread
inline fun CSHasRegistrations.launchRepeat(
    dispatcher: CoroutineDispatcher = Main,
    delay: Duration, after: Duration = delay, start: Boolean = true,
    crossinline function: suspend () -> Unit,
): CSRegistration = launchRepeat(
    dispatcher, delay.inWholeMilliseconds.toInt(),
    after.inWholeMilliseconds.toInt(), start, function
)

@AnyThread
inline fun CSHasRegistrations.launchRepeat(
    delay: Int, after: Int = delay, start: Boolean = true,
    crossinline function: suspend () -> Unit,
): CSRegistration = launchRepeat(
    dispatcher = Main, delay, after, start, function)

@AnyThread
inline fun CSHasRegistrations.launchRepeat(
    dispatcher: CoroutineDispatcher = Main, delay: Int,
    after: Int = delay, start: Boolean = true,
    crossinline function: suspend () -> Unit,
): CSRegistration {
    val channel = Channel<Unit>(Channel.CONFLATED)
    if (start) channel.trySend(Unit)
    val registration = launch(dispatcher) {
        for (signal in channel) {
            delay(after.toLong())
            while (it.isActive) {
                function()
                delay(delay.toLong())
            }
        }
    }
    registration.setActive(start)
    return CSRegistration(
        isActive = start,
        onResume = {
            registration.resume()
            channel.trySend(Unit)
        },
        onPause = {
            registration.pause()
        },
        onCancel = {
            registration.cancel()
            channel.close()
        }
    )
}