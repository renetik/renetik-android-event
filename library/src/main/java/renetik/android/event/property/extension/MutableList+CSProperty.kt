package renetik.android.event.property.extension

import renetik.android.core.kotlin.primitives.update
import renetik.android.event.common.CSHasDestroy
import renetik.android.event.common.destroy
import renetik.android.event.property.CSProperty
import renetik.android.event.property.action
import renetik.android.event.registration.CSRegistration

fun <Item : CSHasDestroy> CSProperty<Int>.updates(
    list: MutableList<Item>, function: (index: Int) -> Item): CSRegistration =
    action { value -> list.update(value, function) }

fun <Item : CSHasDestroy> MutableList<Item>.update(
    value: Int, function: (index: Int) -> Item) = size.update(value,
    onAdd = { index -> add(function(index)) },
    onRemove = { index -> removeAt(index).destroy() })