@file:OptIn(ExperimentalAtomicApi::class)
@file:Suppress("NOTHING_TO_INLINE")

package renetik.android.event.registration

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.Main
import renetik.android.core.kotlin.className
import renetik.android.core.kotlin.primitives.isFalse
import renetik.android.core.kotlin.primitives.isTrue
import renetik.android.core.lang.value.isFalse
import renetik.android.core.lang.value.isTrue
import renetik.android.event.registration.CSHasChangeValue.Companion.delegateValue
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

suspend fun CSHasChangeValue<Boolean>.suspendIfTrue() = waitForFalse()
suspend fun CSHasChangeValue<Boolean>.suspendIfFalse() = waitForTrue()
suspend fun CSHasChangeValue<Boolean>.waitForTrue() = waitFor(Boolean::isTrue)
suspend fun CSHasChangeValue<Boolean>.waitForFalse() = waitFor(Boolean::isFalse)

inline fun CSHasChangeValue<Boolean>.onFalse(crossinline function: () -> Unit): CSRegistration =
    onChange { if (it.isFalse) function() }

inline fun CSHasChangeValue<Boolean>.onTrue(crossinline function: () -> Unit): CSRegistration =
    onChange { if (it.isTrue) function() }

inline fun CSHasChangeValue<Boolean>.isTrue(parent: CSHasRegistrations? = null) =
    delegateValue(parent, from = { it })

@JvmName("CSHasChangeValueOptionalBooleanDelegateIsTrue")
inline fun CSHasChangeValue<Boolean?>.isTrue(
    parent: CSHasRegistrations? = null) = delegateValue(parent, from = { it == true })

inline fun CSHasChangeValue<Boolean>.isFalse(parent: CSHasRegistrations? = null) =
    delegateValue(parent, from = { !it })

inline val CSHasChangeValue<Boolean>.eventIsTrue: CSHasChange<Unit>
    get() = object : CSHasChange<Unit> {
        override fun onChange(function: (Unit) -> Unit) = this@eventIsTrue.onTrue { function(Unit) }
    }

inline val CSHasChangeValue<Boolean>.eventIsFalse: CSHasChange<Unit>
    get() = (!this).eventIsTrue

inline fun CSHasChangeValue<Boolean>.actionTrue(
    crossinline function: (CSRegistration) -> Unit
): CSRegistration {
    val invoked = AtomicBoolean(false)
    val registration = onChange { registration, value ->
        if (value) {
            if (invoked.compareAndSet(false, true)) function(registration)
        } else invoked.set(false)
    }
    if (isTrue) if (invoked.compareAndSet(false, true)) function(registration)
    return registration
}

/**
 * Register edge handlers for a boolean observable and invoke once for the current state.
 *
 * Calls `onTrue` when value becomes `true` and `onFalse` when it becomes `false`.
 * Invocation for the current state happens immediately; thread-safe. Returns a
 * `CSRegistration` to cancel the registration.
 *
 * @param onTrue  called when value becomes true
 * @param onFalse called when value becomes false
 * @return CSRegistration for this registration
 */
inline fun CSHasChangeValue<Boolean>.action(
    crossinline onTrue: (CSRegistration) -> Unit,
    crossinline onFalse: (CSRegistration) -> Unit
): CSRegistration {
    val last = java.util.concurrent.atomic.AtomicInteger(0)
    val registration = onChange { reg, value ->
        val newState = if (value) 1 else 2
        val prev = last.getAndSet(newState)
        if (prev != newState) {
            if (value) onTrue(reg) else onFalse(reg)
        }
    }
    val initial = if (isTrue) 1 else 2
    if (last.compareAndSet(0, initial)) {
        if (initial == 1) onTrue(registration) else onFalse(registration)
    }
    return registration
}

inline fun CSHasChangeValue<Boolean>.actionTrueLaunch(
    dispatcher: CoroutineDispatcher = Main,
    crossinline function: suspend (CSRegistration) -> Unit): CSRegistration =
    CSRegistrationsMap(className).also { registration ->
        registration + actionTrue {
            registration.launch(dispatcher) { function(registration) }
        }
    }

inline fun CSHasChangeValue<Boolean>.actionFalse(
    crossinline function: () -> Unit): CSRegistration {
    val invoked = AtomicBoolean(false)
    val registration = onChange {
        if (it.isFalse) {
            if (invoked.compareAndSet(false, true)) function()
        } else invoked.set(false)
    }
    if (isFalse) if (invoked.compareAndSet(false, true)) function()
    return registration
}

inline fun CSHasChangeValue<Boolean>.actionFalseLaunch(
    dispatcher: CoroutineDispatcher = Main,
    crossinline function: suspend () -> Unit): CSRegistration =
    CSRegistrationsMap(className).also {
        it + actionFalse { it.launch(dispatcher) { function() } }
    }

inline operator fun CSHasChangeValue<Boolean>.not() = delegateValue(from = { it -> !it })

inline fun CSHasChangeValue<Boolean>.onTrueUntilFalse(
    crossinline registration: () -> CSRegistration?): CSRegistration {
    var untilFalseRegistration: CSRegistration? = null
    val actionTrueRegistration = actionTrue {
        untilFalseRegistration?.cancel()
        untilFalseRegistration = untilFalse(registration())
    }
    return CSRegistration {
        actionTrueRegistration.cancel()
        untilFalseRegistration?.cancel()
    }
}

inline fun CSHasChangeValue<Boolean>.untilFalse(registration: CSRegistration): CSRegistration =
    onFalse { registration.cancel() }

@JvmName("untilFalseCSRegistrationNullable")
inline fun CSHasChangeValue<Boolean>.untilFalse(registration: CSRegistration?): CSRegistration? =
    registration?.let { untilFalse(it) }