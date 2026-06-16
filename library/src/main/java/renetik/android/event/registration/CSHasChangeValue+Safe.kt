package renetik.android.event.registration

import renetik.android.event.property.CSSafeHasChangeValue
import renetik.android.event.property.CSSafeHasChangeValueBase

@JvmName("CSHasChangeValueAndCSSafeHasChangeValueBoolean")
infix fun <T> CSHasChangeValue<T>.and(
    other: CSSafeHasChangeValue<Boolean>
): CSSafeHasChangeValue<T> {
    val first = this
    return object : CSSafeHasChangeValueBase<T>(initialValue = first.value) {
        init {
            @Suppress("UNCHECKED_CAST")
            this + (
                if (first is CSSafeHasChangeValue<*>)
                    (first as CSSafeHasChangeValue<T>).onUnsafeChange {
                        if (other.value) value(it)
                        else setValueSilently(it)
                    }
                else first.onChange {
                    if (other.value) value(it)
                    else setValueSilently(it)
                })
            this + other.onUnsafeChange {
                if (it) {
                    val currentValue = value
                    value(currentValue, force = true)
                }
            }
        }
    }
}

@JvmName("CSSafeHasChangeValueAndCSSafeHasChangeValueBoolean")
infix fun <T> CSSafeHasChangeValue<T>.and(
    other: CSSafeHasChangeValue<Boolean>
): CSSafeHasChangeValue<T> {
    val first = this
    return object : CSSafeHasChangeValueBase<T>(initialValue = first.value) {
        init {
            this + first.onUnsafeChange {
                if (other.value) value(it)
                else setValueSilently(it)
            }
            this + other.onUnsafeChange {
                if (it) {
                    val currentValue = value
                    value(currentValue, force = true)
                }
            }
        }
    }
}

@JvmName("CSSafeHasChangeValueAndCSHasChangeValueBoolean")
infix fun <T> CSSafeHasChangeValue<T>.and(
    other: CSHasChangeValue<Boolean>
): CSSafeHasChangeValue<T> {
    val first = this
    return object : CSSafeHasChangeValueBase<T>(initialValue = first.value) {
        init {
            this + first.onUnsafeChange {
                if (other.value) value(it)
                else setValueSilently(it)
            }
            this + other.onChange {
                if (it) {
                    val currentValue = value
                    value(currentValue, force = true)
                }
            }
        }
    }
}

@JvmName("CSSafeHasChangeValueBooleanNot")
operator fun CSSafeHasChangeValue<Boolean>.not(): CSSafeHasChangeValue<Boolean> {
    val source = this
    return object : CSSafeHasChangeValueBase<Boolean>(initialValue = !source.value) {
        init {
            this + source.onUnsafeChange { value(!it) }
        }
    }
}
