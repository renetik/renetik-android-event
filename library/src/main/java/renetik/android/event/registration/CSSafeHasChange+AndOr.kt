package renetik.android.event.registration

import renetik.android.core.kotlin.primitives.isTrue
import renetik.android.event.property.CSSafeHasChangeValue
import renetik.android.event.property.CSSafeHasChangeValueBase

@JvmName("BooleanAndCSSafeHasChangeValueBoolean")
infix fun Boolean.and(other: CSSafeHasChangeValue<Boolean>): CSSafeHasChangeValue<Boolean> =
    other.delegateValue(from = { this && it })

@JvmName("CSSafeHasChangeValueBooleanAndBoolean")
infix fun CSSafeHasChangeValue<Boolean>.and(other: Boolean): CSSafeHasChangeValue<Boolean> =
    delegateValue(from = { it && other })

@JvmName("CSSafeHasChangeValueBooleanAndCSSafeHasChangeValueBooleanLogical")
infix fun CSSafeHasChangeValue<Boolean>.and(
    other: CSSafeHasChangeValue<Boolean>
): CSSafeHasChangeValue<Boolean> =
    (this to other).delegate(from = { first, second -> first && second })

@JvmName("CSSafeHasChangeValueBooleanAndCSSafeHasChangeValueBooleanNullable")
infix fun CSSafeHasChangeValue<Boolean>.and(
    other: CSSafeHasChangeValue<Boolean?>
): CSSafeHasChangeValue<Boolean> =
    (this to other).delegate(from = { first, second -> first && second.isTrue })

@JvmName("CSHasChangeValueAndCSSafeHasChangeValueBoolean")
infix fun <T> CSHasChangeValue<T>.and(
    other: CSSafeHasChangeValue<Boolean>
): CSSafeHasChangeValue<T> {
    val first = this
    return object : CSSafeHasChangeValueBase<T>(initialValue = first.value) {
        init {
            this + first.onChange {
                if (other.value) value(it)
                else setValueSilently(it)
            }
            this + other.onUnsafeChange {
                if (it) value(value, force = true)
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
                if (it) value(value, force = true)
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
                if (it) value(value, force = true)
            }
        }
    }
}

@JvmName("CSSafeHasChangeValueIfTrueCSSafeHasChangeValueBoolean")
infix fun <T> CSSafeHasChangeValue<T>.ifTrue(
    other: CSSafeHasChangeValue<Boolean>
): CSSafeHasChangeValue<T?> =
    (this to other).delegate(from = { first, second ->
        if (second) first else null
    })

@JvmName("CSSafeHasChangeValueIfFalseCSSafeHasChangeValueBoolean")
infix fun <T> CSSafeHasChangeValue<T>.ifFalse(
    other: CSSafeHasChangeValue<Boolean>
): CSSafeHasChangeValue<T?> =
    (this to other).delegate(from = { first, second ->
        if (!second) first else null
    })

@JvmName("CSSafeHasChangeValueBooleanOrCSSafeHasChangeValueBoolean")
infix fun CSSafeHasChangeValue<Boolean>.or(
    other: CSSafeHasChangeValue<Boolean>
): CSSafeHasChangeValue<Boolean> =
    (this to other).delegate(from = { first, second -> first || second })

@JvmName("CSSafeHasChangeValueOrCSHasChange")
infix fun <T> CSSafeHasChangeValue<T>.or(
    second: CSHasChange<*>
): CSSafeHasChangeValue<T> {
    val first = this
    return object : CSSafeHasChangeValueBase<T>(initialValue = first.value) {
        init {
            this + first.onUnsafeChange { value(it) }
            this + second.onChange { value(value, force = true) }
        }
    }
}
