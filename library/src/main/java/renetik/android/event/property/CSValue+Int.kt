package renetik.android.event.property

import renetik.android.core.kotlin.primitives.update
import renetik.android.core.lang.ArgFunc
import renetik.android.core.lang.value.CSValue
import renetik.android.event.common.CSHasDestruct
import renetik.android.event.common.destruct

fun CSValue<Int>.update(
    newCount: Int, onAdd: ArgFunc<Int>? = null, onRemove: ArgFunc<Int>? = null
) = value.update(newCount, onAdd, onRemove)

fun <Item : CSHasDestruct> CSValue<Int>.update(
    list: MutableList<Item>, function: (index: Int) -> Item
) = list.size.update(value,
    onAdd = { index -> list.add(function(index)) },
    onRemove = { index -> list.removeAt(index).destruct() })

operator fun CSValue<Int>.times(value: Int): Int = this.value * value
operator fun CSValue<Int>.div(value: Int): Int = this.value / value
operator fun CSValue<Int>.plus(value: Int): Int = this.value + value
operator fun CSValue<Int>.minus(value: Int): Int = this.value - value
operator fun CSValue<Int>.minus(value: CSValue<Int>): Int = this.value - value.value
