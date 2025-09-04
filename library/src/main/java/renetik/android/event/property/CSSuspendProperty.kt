package renetik.android.event.property

import renetik.android.core.kotlin.notImplemented
import renetik.android.core.lang.value.CSValue
import renetik.android.event.registration.CSSuspendHasChange
import renetik.android.event.registration.CSSuspendHasChangeValue

interface CSSuspendProperty<T> : CSValue<T>, CSSuspendHasChange<T>, CSSuspendHasChangeValue<T> {
    companion object {
        fun <T> suspendProperty(
            value: T, onChange: suspend ((value: T) -> Unit) = {}
        ) = CSSuspendPropertyImpl(value, onChange)
    }

    suspend fun value(newValue: T, fire: Boolean = true)
    suspend fun fireChange()
    fun pause(): Unit = notImplemented()
    suspend fun resume(fireChange: Boolean = true): Unit = notImplemented()
}