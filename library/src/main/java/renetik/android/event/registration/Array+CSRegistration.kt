package renetik.android.event.registration

fun <T : CSRegistration> Array<T>.pause() {
    forEach { it.pause() }
}

fun <T : CSRegistration> Array<T>.resume() {
    forEach { it.resume() }
}
