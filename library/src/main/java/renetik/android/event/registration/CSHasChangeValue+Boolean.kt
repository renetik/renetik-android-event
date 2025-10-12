@file:OptIn(ExperimentalAtomicApi::class)

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

fun CSHasChangeValue<Boolean>.onFalse(function: () -> Unit): CSRegistration =
    onChange { if (it.isFalse) function() }


fun CSHasChangeValue<Boolean>.onTrue(function: () -> Unit): CSRegistration =
    onChange { if (it.isTrue) function() }

fun CSHasChangeValue<Boolean>.isTrue(parent: CSHasRegistrations? = null) =
    delegateValue(parent, from = { it -> it })

@JvmName("CSHasChangeValueOptionalBooleanDelegateIsTrue") fun CSHasChangeValue<Boolean?>.isTrue(
    parent: CSHasRegistrations? = null) = delegateValue(parent, from = { it == true })

fun CSHasChangeValue<Boolean>.isFalse(parent: CSHasRegistrations? = null) =
    delegateValue(parent, from = { !it })

val CSHasChangeValue<Boolean>.eventIsTrue: CSHasChange<Unit>
    get() = object : CSHasChange<Unit> {
        override fun onChange(function: (Unit) -> Unit) = this@eventIsTrue.onTrue { function(Unit) }
    }

val CSHasChangeValue<Boolean>.eventIsFalse: CSHasChange<Unit>
    get() = (!this).eventIsTrue

fun CSHasChangeValue<Boolean>.actionTrue(function: (CSRegistration) -> Unit): CSRegistration {
    val invoked = AtomicBoolean(false)
    val registration = onChange { registration, value ->
        if (value) {
            if (invoked.compareAndSet(false, true)) function(registration)
        } else invoked.set(false)
    }
    if (isTrue) if (invoked.compareAndSet(false, true)) function(registration)
    return registration
}

fun CSHasChangeValue<Boolean>.actionTrueLaunch(
    dispatcher: CoroutineDispatcher = Main,
    function: suspend (CSRegistration) -> Unit): CSRegistration =
    CSRegistrationsMap(className).also { registration ->
        registration + actionTrue {
            registration.launch(dispatcher) { function(registration) }
        }
    }

fun CSHasChangeValue<Boolean>.actionFalse(function: () -> Unit): CSRegistration {
    val invoked = AtomicBoolean(false)
    val registration = onChange {
        if (it.isFalse) {
            if (invoked.compareAndSet(false, true)) function()
        } else invoked.set(false)
    }
    if (isFalse) if (invoked.compareAndSet(false, true)) function()
    return registration
}

fun CSHasChangeValue<Boolean>.actionFalseLaunch(
    dispatcher: CoroutineDispatcher = Main,
    function: suspend () -> Unit): CSRegistration =
    CSRegistrationsMap(className).also {
        it + actionFalse { it.launch(dispatcher) { function() } }
    }

operator fun CSHasChangeValue<Boolean>.not() = delegateValue(from = { it -> !it })

fun CSHasChangeValue<Boolean>.onTrueUntilFalse(registration: () -> CSRegistration?): CSRegistration {
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

fun CSHasChangeValue<Boolean>.untilFalse(registration: CSRegistration): CSRegistration =
    onFalse { registration.cancel() }

@JvmName("untilFalseCSRegistrationNullable")
fun CSHasChangeValue<Boolean>.untilFalse(registration: CSRegistration?): CSRegistration? =
    registration?.let { untilFalse(it) }