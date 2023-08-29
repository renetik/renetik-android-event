package renetik.android.event.registration

import java.io.Closeable
import renetik.android.core.lang.Func
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

fun CSRegistration(vararg registrations: CSRegistration) =
    CSRegistration(registrations.asList())

fun CSRegistration(registrations: List<CSRegistration>) = CSRegistration(
    isActive = true,
    onResume = { registrations.forEach { it.resume() } },
    onPause = { registrations.forEach { if (it.isActive) it.pause() } },
    onCancel = { registrations.forEach { it.cancel() } })

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

val CSRegistration.isResumed get() = isActive
val CSRegistration.isPaused get() = !isActive
fun CSRegistration.start() = apply { resume() }