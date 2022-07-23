package renetik.android.event.registration

import renetik.android.core.lang.Func
import java.io.Closeable

fun List<CSRegistration>.paused(function: Func) {
    onEach { it.pause() }
    function()
    onEach { it.resume() }
}

fun CSRegistration.paused(function: Func) {
    pause()
    function()
    resume()
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