package renetik.android.event

import androidx.annotation.UiThread
import renetik.android.event.registration.CSRegistration

interface CSEvent<T> {

    companion object {
        @JvmName("eventWithArgument")
        fun <T> event(): CSEvent<T> = CSEventImpl()

        fun event(): CSEvent<Unit> = CSEventImpl()
    }

    fun pause()

    fun resume()

    val isListened: Boolean

    fun listen(@UiThread function: (argument: T) -> Unit): CSRegistration

    fun fire(argument: T)

    fun clear()
}