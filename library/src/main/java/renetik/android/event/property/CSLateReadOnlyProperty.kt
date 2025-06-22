package renetik.android.event.property

import renetik.android.core.lang.value.CSValue
import renetik.android.event.common.CSHasDestruct
import renetik.android.event.util.CSLater.later
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class CSLateReadOnlyProperty<T>(
    parent: CSHasDestruct,
    create: () -> CSValue<T>
) : ReadOnlyProperty<Any?, T> {
    companion object {
        fun <T> CSHasDestruct.lateValue(create: () -> CSValue<T>) =
            CSLateReadOnlyProperty(this, create)
    }

    lateinit var property: CSValue<T>

    init {
        parent.later { property = create() }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T =
        synchronized(this) { this.property.value }
}

