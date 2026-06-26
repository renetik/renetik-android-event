package renetik.android.event.change

import renetik.android.event.dispatch.*
import renetik.android.event.registration.*
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

fun <T> CSHasChangeValue<T>.delegate(
    parent: CSHasRegistrations? = null,
): CSHasChangeValue<T> = this@delegate.delegate(parent, fromValue = { it })

fun <T> CSHasChangeValue<T>.delegateIsChange(
    parent: CSHasRegistrations? = null,
): CSHasChangeValue<Boolean> = let { property ->
    object : CSHasChangeValue<Boolean> {
        override var value: Boolean = false
        override fun onChange(function: (Boolean) -> Unit): CSRegistration =
            property.onChange {
                value = true
                if (parent?.registrations.isActive) function(true)
                value = false
            }.registerTo(parent)
    }
}