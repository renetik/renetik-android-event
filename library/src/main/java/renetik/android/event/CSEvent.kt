package renetik.android.event

import androidx.annotation.AnyThread
import renetik.android.event.common.CSHasDestruct
import renetik.android.event.registration.CSHasChange
import renetik.android.event.registration.CSRegistration

interface CSEvent<T> : CSHasChange<T> {

    companion object {
        @JvmName("eventWithArgument") fun <T> event() = CSEventImpl<T>()

        fun event() = CSEventImpl<Unit>()

        @JvmName("eventWithArgument")
        fun <T> CSHasDestruct.safeEvent() = CSEventImpl<T>().onMain(this)

        fun CSHasDestruct.safeEvent() = CSEventImpl<Unit>().onMain(this)

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

    @AnyThread
    fun pause()

    @AnyThread
    fun resume()

    @get:AnyThread
    val isListened: Boolean

    @AnyThread
    fun listen(function: (argument: T) -> Unit): CSRegistration

    @AnyThread
    fun fire(argument: T)

    @AnyThread
    fun clear()
}