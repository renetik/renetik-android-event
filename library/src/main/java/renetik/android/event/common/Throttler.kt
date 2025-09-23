package renetik.android.event.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.withTimeoutOrNull
import renetik.android.core.logging.CSLog.logError
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.launch
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.TimeSource

class Throttler<T>(
    parent: CSHasRegistrations,
    dispatcher: CoroutineDispatcher = Main,
    private val action: suspend (T) -> Unit,
    private val after: Duration = ZERO,
) : (T) -> Unit {
    companion object {
        fun CSHasRegistrations.throttler(
            after: Duration, function: suspend () -> Unit
        ) = Throttler<Unit>(this, Main, { function() }, after)

        fun CSHasRegistrations.throttler(
            dispatcher: CoroutineDispatcher = Main,
            after: Duration, function: suspend () -> Unit
        ) = Throttler<Unit>(this, dispatcher, { function() }, after)

        fun CSHasRegistrations.throttler(
            dispatcher: CoroutineDispatcher = Main, function: suspend () -> Unit
        ) = Throttler<Unit>(this, dispatcher, { function() })

        fun CSHasRegistrations.throttler(
            function: suspend () -> Unit
        ) = Throttler<Unit>(this, Main, { function() })

        fun <T> CSHasRegistrations.throttler(
            after: Duration, function: suspend (T) -> Unit
        ) = Throttler(this, Main, function, after)

        fun <T> CSHasRegistrations.throttler(
            dispatcher: CoroutineDispatcher = Main,
            after: Duration, function: suspend (T) -> Unit
        ) = Throttler(this, dispatcher, function, after)

        fun <T> CSHasRegistrations.throttler(
            dispatcher: CoroutineDispatcher = Main, function: suspend (T) -> Unit
        ) = Throttler(this, dispatcher, function)

        fun <T> CSHasRegistrations.throttler(
            function: suspend (T) -> Unit
        ) = Throttler(this, Main, function)

        operator fun Throttler<Unit>.invoke() = invoke(Unit)
    }

    private val channel = Channel<T>(Channel.CONFLATED)

    init {
        parent.launch(dispatcher) {
            val clock = TimeSource.Monotonic
            while (it.isActive) {
                var param = try {
                    channel.receive()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: ClosedReceiveChannelException) {
                    break
                }
                if (after == ZERO) {
                    runCatching { action(param) }.onFailure(::logError)
                    continue
                }
                val mark = clock.markNow()
                while (it.isActive) {
                    val remaining = after - mark.elapsedNow()
                    if (remaining <= ZERO) break
                    val timeoutMs = remaining.inWholeMilliseconds.coerceAtLeast(1L)
                    param = withTimeoutOrNull(timeoutMs) { channel.receive() } ?: break
                }
                runCatching { action(param) }.onFailure(::logError)
            }
        }
    }

    override fun invoke(param: T) {
        channel.trySend(param)
    }
}

operator fun Throttler<Unit>.invoke() = invoke(Unit)