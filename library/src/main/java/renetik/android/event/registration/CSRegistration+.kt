package renetik.android.event.registration

import renetik.android.core.lang.Func
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import java.io.Closeable

val CSRegistrationEmpty = object : CSRegistration {
    override val isActive: Boolean = false
    override val isCanceled: Boolean = false
    override fun resume() = Unit
    override fun pause() = Unit
    override fun cancel() = Unit
}

fun CSRegistration(vararg registrations: CSRegistration?) =
    CSRegistration(registrations.asIterable())

fun CSRegistration(registrations: Iterable<CSRegistration?>) = CSRegistration(
    isActive = true,
    onResume = { registrations.forEach { it?.resume() } },
    onPause = { registrations.forEach { if (it?.isActive == true) it.pause() } },
    onCancel = { registrations.forEach { it?.cancel() } })

fun CSRegistration(
    vararg registrations: () -> CSRegistration?,
) = CSRegistration(isActive = true,
    onPause = { registrations.forEach { it()?.pause() } },
    onResume = { registrations.forEach { it()?.resume() } },
    onCancel = { registrations.forEach { it()?.cancel() } }
)

inline fun List<CSRegistration>.paused(function: Func) {
    onEach { it.pause() }
    function()
    onEach { it.resume() }
}

inline fun CSRegistration.paused(function: Func) {
    pause()
    function()
    if (!isCanceled) resume()
}

fun CSRegistration.setActive(active: Boolean) {
    if (active) resume() else pause()
}

fun CSRegistration.paused(): Closeable {
    pause()
    return Closeable { resume() }
}

fun CSRegistration.start() = apply { resume() }