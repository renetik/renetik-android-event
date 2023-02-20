package renetik.android.event.registration
@Deprecated("use list")
fun <T : CSRegistration> Array<T>.pause() {
    forEach { it.pause() }
}

@Deprecated("use list")
fun <T : CSRegistration> Array<T>.resume() {
    forEach { it.resume() }
}

fun <T : CSRegistration> List<T>.pause() {
    forEach { it.pause() }
}

fun <T : CSRegistration> List<T>.resume() {
    forEach { it.resume() }
}
