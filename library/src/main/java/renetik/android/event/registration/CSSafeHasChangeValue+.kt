@file:Suppress("NOTHING_TO_INLINE")
@file:OptIn(ExperimentalAtomicApi::class)

package renetik.android.event.registration

import kotlinx.coroutines.suspendCancellableCoroutine
import renetik.android.core.lang.ArgFun
import renetik.android.event.common.CSHasRegistrationsHasDestruct
import renetik.android.event.property.CSSafeHasChangeValue
import renetik.android.event.property.CSSafeHasChangeValueBase
import kotlin.Result.Companion.success
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

inline fun <T> CSSafeHasChangeValue<T?>.isNull(): CSSafeHasChangeValue<Boolean> = isSetTo(null)

inline fun <T> CSSafeHasChangeValue<T?>.isNotNull(): CSSafeHasChangeValue<Boolean> = !isSetTo(null)

inline infix fun <T> CSSafeHasChangeValue<T>.isSetTo(value: T): CSSafeHasChangeValue<Boolean> =
    delegateValue(from = { it == value })

suspend fun <T> CSSafeHasChangeValue<T>.waitFor(condition: (T) -> Boolean) {
    if (!condition(value)) suspendCancellableCoroutine { coroutine ->
        val registration = AtomicReference<CSRegistration?>(null)
        fun resume() = registration.exchange(null)?.apply {
            cancel()
            coroutine.resumeWith(success(Unit))
        }
        registration.store(onUnsafeChange { if (condition(value)) resume() })
        if (condition(value)) resume()
        coroutine.invokeOnCancellation { registration.exchange(null)?.cancel() }
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

@JvmName("safeHasChangeValue")
fun <Argument, Return> CSSafeHasChangeValue<Argument>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    unsafeFrom: (Argument) -> Return,
    onChange: ArgFun<Return>? = null
): CSSafeHasChangeValue<Return> = let { source ->
    object : CSSafeHasChangeValueBase<Return>(parent, unsafeFrom(source.value), onChange) {
        init {
            this + source.onUnsafeChange { value(unsafeFrom(it)) }
        }
    }
}

inline fun <T> CSSafeHasChangeValue<T>.unsafeAction(noinline function: (T) -> Unit): CSRegistration {
    onValue(function)
    return onUnsafeChange(function)
}

@JvmName("safeHasChangeValueIdentity")
fun <T> CSSafeHasChangeValue<T>.hasChangeValue(
    parent: CSHasRegistrations? = null, onChange: ArgFun<T>? = null,
): CSSafeHasChangeValue<T> = hasChangeValue(parent, unsafeFrom = { it }, onChange)

fun <Argument, Return> CSSafeHasChangeValue<Argument>.hasUnsafeChangeValue(
    parent: CSHasRegistrationsHasDestruct,
    from: (Argument) -> Return
): CSSafeHasChangeValue<Return> = let { source ->
    object : CSSafeHasChangeValueBase<Return>(parent, from(source.value)) {
        init {
            parent + source.onUnsafeChange {
                value(from(it))
            }
        }
    }
}