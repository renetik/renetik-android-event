package renetik.android.event

import renetik.android.event.registration.CSHasChange
import renetik.android.event.registration.CSRegistration

interface CSEvent<T> : CSHasChange<T> {

    companion object {
        @JvmName("eventWithArgument")
        fun <T> event(): CSEvent<T> = CSEventImpl()

        fun event(): CSEvent<Unit> = CSEventImpl()

        val Empty: CSEvent<Unit> = empty()

        fun <T> empty(): CSEvent<T> = object : CSEvent<T> {
            override fun pause() = Unit
            override fun resume() = Unit
            override val isListened: Boolean = false
            override fun clear() = Unit
            override fun onChange(function: (T) -> Unit) = CSRegistration.Empty
            override fun fire(argument: T) = Unit
            override fun listen(function: (argument: T) -> Unit) = CSRegistration.Empty
        }
    }

    fun pause()

    fun resume()

    val isListened: Boolean

    fun listen(function: (argument: T) -> Unit): CSRegistration

    fun fire(argument: T)

    fun clear()
}