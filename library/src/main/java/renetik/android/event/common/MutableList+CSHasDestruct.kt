package renetik.android.event.common

import renetik.android.core.kotlin.primitives.update
import renetik.android.event.CSEvent
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.invoke
import renetik.android.event.registration.CSHasChangeValue
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.action
import renetik.android.event.registration.plus

fun <Item : CSHasDestruct> MutableList<Item>.update(
    count: Int, function: (index: Int) -> Item
) = size.update(count,
    onAdd = { index -> this += function(index) },
    onRemove = { index -> removeAt(index).destruct() })

fun <T : CSHasDestruct> MutableList<T>.factory(
    parent: CSHasRegistrations,
    count: CSHasChangeValue<Int>, create: (Int) -> T
) = apply {
    parent + count.action { count -> update(count, create) }
}

fun <T : CSHasDestruct> MutableList<T>.update(
    count: Int, eventAdded: CSEvent<T>, function: (index: Int) -> T
) = size.update(count,
    onAdd = { index -> this += function(index).also { eventAdded(it) } },
    onRemove = { index -> removeAt(index).destruct() })

fun <T : CSHasDestruct> MutableList<T>.update(
    count: Int, eventAdded: CSEvent<T>,
    eventRemoved: CSEvent<Int>, function: (index: Int) -> T
) = size.update(count,
    onAdd = { index -> this += function(index).also { eventAdded(it) } },
    onRemove = { index -> removeAt(index).destruct().also { eventRemoved(index) } })

fun <T : CSHasDestruct> MutableList<T>.factory(
    parent: CSHasRegistrations,
    count: CSHasChangeValue<Int>,
    eventAdded: CSEvent<T>,
    eventRemoved: CSEvent<Int> = event<Int>(),
    create: (Int) -> T,
) = apply {
    parent + count.action { count ->
        update(count, eventAdded, eventRemoved, create)
    }
}

fun <Item : CSHasDestruct> MutableList<Item>.updateOnAdd(
    value: Int, function: (index: Int) -> Item
) = size.update(value,
    onAdd = { index -> add(function(index)) })

fun <Item : CSHasDestruct> MutableList<Item>.updateOnRemove(
    value: Int, function: (index: Int) -> Item
) = size.update(value,
    onRemove = { index -> add(function(index)) })