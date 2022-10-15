package renetik.android.event.common

import renetik.android.core.kotlin.primitives.update

fun <Item : CSHasDestruct> MutableList<Item>.update(
    value: Int, function: (index: Int) -> Item) = size.update(value,
    onAdd = { index -> add(function(index)) },
    onRemove = { index -> removeAt(index).destroy() })

fun <Item : CSHasDestruct> MutableList<Item>.updateOnAdd(
    value: Int, function: (index: Int) -> Item) = size.update(value,
    onAdd = { index -> add(function(index)) })

fun <Item : CSHasDestruct> MutableList<Item>.updateOnRemove(
    value: Int, function: (index: Int) -> Item) = size.update(value,
    onRemove = { index -> add(function(index)) })