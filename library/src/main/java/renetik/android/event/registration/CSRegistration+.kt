package renetik.android.event.registration

import renetik.android.core.lang.Func
import java.io.Closeable

fun List<CSRegistration>.pause(function: Func) {
    onEach { it.isActive = false }
    function()
    onEach { it.isActive = true }
}

fun CSRegistration.pause(): Closeable {
    isActive = false
    return Closeable { resume() }
}

fun CSRegistration.pause(function: Func) {
    isActive = false
    function()
    isActive = true
}

fun CSRegistration.resume() = apply {
    isActive = true
}