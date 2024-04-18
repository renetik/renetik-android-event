package renetik.android.event

import renetik.android.event.registration.CSHasChange
import renetik.android.event.registration.CSRegistration

interface CSEvent<T> : CSHasChange<T> {

    companion object {
        @JvmName("eventWithArgument")
        fun <T> event(): CSEvent<T> = CSEventImpl()

        fun event(): CSEvent<Unit> = CSEventImpl()
    }

    fun pause()

    fun resume()

    val isListened: Boolean

    fun listen(function: (argument: T) -> Unit): CSRegistration

    fun fire(argument: T)

    fun clear()
}