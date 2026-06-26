@file:Suppress("NOTHING_TO_INLINE")

package renetik.android.event.change

import renetik.android.event.dispatch.*
import renetik.android.event.registration.*
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

import renetik.android.core.lang.value.CSValue

interface CSHasChangeValue<T> : CSValue<T>, CSHasChange<T> {
    companion object {
        inline fun <T> emptyNullable() = object : CSHasChangeValue<T?> {
            override val value: T? = null
            override fun onChange(function: (T?) -> Unit) = CSRegistration.Empty
        }

        inline fun <T> empty(value: T) = object : CSHasChangeValue<T> {
            override val value: T = value
            override fun onChange(function: (T) -> Unit) = CSRegistration.Empty
        }

    }
}
