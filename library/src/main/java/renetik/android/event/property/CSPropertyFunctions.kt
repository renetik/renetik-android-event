package renetik.android.event.property

object CSPropertyFunctions {
    fun <T> property(value: T, onApply: ((value: T) -> Unit)? = null) =
        CSPropertyImpl(value, onApply)

    fun <T> property(onApply: ((value: T?) -> Unit)? = null) =
        CSPropertyImpl<T?>(null, onApply)

    fun <T> synchronizedProperty(value: T, onApply: ((value: T) -> Unit)? = null)
            : CSSynchronizedProperty<T> = CSSynchronizedPropertyImpl(value, onApply)

    fun <T> synchronizedProperty(property: CSProperty<T>,
                                 onApply: ((value: T) -> Unit)? = null) =
        synchronizedProperty(property.value, onApply).apply { connect(property) }

    fun <T> lateProperty(onApply: ((value: T) -> Unit)? = null) =
        CSLateProperty(onApply)

    fun <T> nullableProperty(onApply: ((value: T?) -> Unit)? = null)
            : CSProperty<T?> = CSPropertyImpl(null, onApply)
}