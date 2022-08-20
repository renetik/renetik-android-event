package renetik.android.event.registration

import renetik.android.core.lang.Func
import java.io.Closeable

fun CSRegistration(vararg registrations: CSRegistration) = CSRegistration.CSRegistration(
    isActive = true,
    onResume = { registrations.forEach { it.resume() } },
    onPause = { registrations.forEach { if (it.isActive) it.pause() } },
    onCancel = { registrations.forEach { it.cancel() } })

fun List<CSRegistration>.paused(function: Func) {
    onEach { it.pause() }
    function()
    onEach { it.resume() }
}

fun CSRegistration.paused(function: Func) {
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