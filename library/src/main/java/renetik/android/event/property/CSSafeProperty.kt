package renetik.android.event.property

import androidx.annotation.AnyThread
import renetik.android.core.lang.variable.CSSafeVariable
import renetik.android.event.common.CSHasDestruct
import renetik.android.event.registration.plus

interface CSSafeProperty<T> : CSSafeHasChangeValue<T>, CSSafeVariable<T>, CSProperty<T> {
    fun getAndSet(newValue: T): T
    fun compareAndSet(value: T, newValue: T): Boolean

    companion object {
        fun <T> CSHasDestruct.safe(
            property: CSProperty<T>, @AnyThread onChangeUnsafe: ((value: T) -> Unit)? = null
        ): CSSafeProperty<T> = property.safe(this, onChangeUnsafe)


        fun <T> CSProperty<T>.safe(
            parent: CSHasDestruct, @AnyThread onChangeUnsafe: ((value: T) -> Unit)? = null
        ): CSSafeProperty<T> = CSSafePropertyImpl(parent, value, onChangeUnsafe)
            .also { it + connect(this) }
    }
}