package renetik.android.event.property

import renetik.android.core.kotlin.collections.mutable

fun <T> CSProperty<List<T>>.remove(item: T) {
    value(value.mutable().apply { remove(item) })
}

fun <T> CSProperty<List<T>>.add(item: T) {
    value(value.mutable().apply { add(item) })
}
