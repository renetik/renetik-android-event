package renetik.android.event.common

import renetik.android.core.kotlin.primitives.update

fun <Item : CSHasDestroy> MutableList<Item>.update(
    value: Int, function: (index: Int) -> Item) = size.update(value,
    onAdd = { index -> add(function(index)) },
    onRemove = { index -> removeAt(index).destroy() })

fun <Item : CSHasDestroy> MutableList<Item>.updateOnAdd(
    value: Int, function: (index: Int) -> Item) = size.update(value,
    onAdd = { index -> add(function(index)) })

fun <Item : CSHasDestroy> MutableList<Item>.updateOnRemove(
    value: Int, function: (index: Int) -> Item) = size.update(value,
    onRemove = { index -> add(function(index)) })