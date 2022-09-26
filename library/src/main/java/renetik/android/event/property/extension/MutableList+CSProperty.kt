package renetik.android.event.property.extension

import renetik.android.core.kotlin.primitives.update
import renetik.android.event.common.CSHasDestroy
import renetik.android.event.common.destroy
import renetik.android.event.property.CSProperty
import renetik.android.event.property.action
import renetik.android.event.registration.CSRegistration

@Deprecated("use fun <Item : CSHasDestroy> CSProperty<Int>.updated")
fun <Item : CSHasDestroy> MutableList<Item>.updated(
    property: CSProperty<Int>, function: (index: Int) -> Item): CSRegistration =
    property.action { value ->
        size.update(value, onAdd = { index -> add(function(index)) },
            onRemove = { index -> removeAt(index).destroy() })
    }

fun <Item : CSHasDestroy> CSProperty<Int>.updating(
    list: MutableList<Item>, function: (index: Int) -> Item): CSRegistration =
    action { value ->
        list.size.update(value, onAdd = { index -> list.add(function(index)) },
            onRemove = { index -> list.removeAt(index).destroy() })
    }