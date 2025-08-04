package renetik.android.event.property

import renetik.android.core.lang.ArgFunc
import renetik.android.core.lang.value.CSSafeValue
import renetik.android.event.common.CSHasDestruct
import renetik.android.event.registration.CSHasChangeValue
import renetik.android.event.registration.plus
import renetik.android.event.util.CSLater.onMain
import kotlin.reflect.KProperty

interface CSSafeHasChangeValue<T> : CSSafeValue<T>, CSHasChangeValue<T> {

    companion object {
        fun <T> CSHasDestruct.safe(property: CSHasChangeValue<T>)
                : CSSafeHasChangeValue<T> = property.safe(this)

        fun <T> CSHasChangeValue<T>.safe(
            parent: CSHasDestruct,
            onChange: ArgFunc<T>? = null
        ): CSSafeHasChangeValue<T> = let { property ->
            object : CSPropertyBase<T>(parent, onChange), CSSafeHasChangeValue<T> {
                @Volatile
                override var value: T = property.value
                override fun getValue(thisRef: Any?, property: KProperty<*>): T = value

                init {
                    this + property.onChange { value(it) }
                }

                override fun onValueChanged(newValue: T, fire: Boolean) {
                    isChanged = true
                    if (fire) onMain(::fireChange)
                }
            }
        }

    }
}