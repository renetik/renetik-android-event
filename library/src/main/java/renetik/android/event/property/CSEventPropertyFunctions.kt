package renetik.android.event.property

object CSEventPropertyFunctions {
    fun <T> property(value: T, onApply: ((value: T) -> Unit)? = null) =
        CSEventPropertyImpl(value, onApply)

    fun <T> property(onApply: ((value: T?) -> Unit)? = null) =
        CSEventPropertyImpl<T?>(null, onApply)

    fun <T> synchronizedProperty(value: T, onApply: ((value: T) -> Unit)? = null)
            : CSSynchronizedEventProperty<T> = CSSynchronizedEventPropertyImpl(value, onApply)

    fun <T> synchronizedProperty(property: CSEventProperty<T>,
                                 onApply: ((value: T) -> Unit)? = null) =
        synchronizedProperty(property.value, onApply).apply { connect(property) }

    fun <T> lateProperty(onApply: ((value: T) -> Unit)? = null) =
        CSLateEventProperty(onApply)

    fun <T> nullableProperty(onApply: ((value: T?) -> Unit)? = null)
            : CSEventProperty<T?> = CSEventPropertyImpl(null, onApply)
}