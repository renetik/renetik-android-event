package renetik.android.event.property

import renetik.android.event.common.CSHasDestruct
import renetik.android.event.registration.CSHasChangeValue
import renetik.android.event.util.CSLater.later
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class CSLateReadOnlyProperty<T>(
    parent: CSHasDestruct,
    create: () -> CSHasChangeValue<T>
) : ReadOnlyProperty<Any?, T> {
    companion object {
        fun <T> CSHasDestruct.lateValue(create: () -> CSHasChangeValue<T>) =
            CSLateReadOnlyProperty(this, create)
    }

    lateinit var property: CSHasChangeValue<T>

    init {
        parent.later { property = create() }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T =
        synchronized(this) { this.property.value }
}