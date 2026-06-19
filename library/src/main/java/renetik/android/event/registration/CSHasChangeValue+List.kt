@file:Suppress("NOTHING_TO_INLINE")

package renetik.android.event.registration

import renetik.android.core.kotlin.className
import renetik.android.core.lang.ArgFun

inline fun <T : CSHasChangeValue<Value>, Value> List<T>.onChange(
    crossinline function: ArgFun<List<Value>>
): CSRegistration = CSRegistrationsMap(className).also { registrations ->
    forEach { item: CSHasChangeValue<Value> ->
        registrations + item.onChange {
            if (registrations.isActive) function(map { it.value })
        }
    }
}

inline fun <T : CSHasChangeValue<Value>, Value> List<T>.action(
    crossinline function: ArgFun<List<Value>>
): CSRegistration {
    function(map { it.value })
    return onChange(function)
}
