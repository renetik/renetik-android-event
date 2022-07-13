package renetik.android.event

import androidx.annotation.UiThread
import renetik.android.event.registration.CSRegistration
import java.io.Closeable

interface CSEvent<T> {

	companion object {
		@JvmName("eventWithType")
		fun <T> event(): CSEvent<T> = CSEventImpl()

		fun event(): CSEvent<Unit> = CSEventImpl()
	}

	fun pause(): Closeable

	fun resume()

	val isListened: Boolean

	fun listen(@UiThread function: (argument: T) -> Unit): CSRegistration

	fun fire(argument: T)

	fun clear()

	@Deprecated("Just for debugging")
	val registrations: List<CSRegistration>
}