package renetik.android.event.registration

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks
import renetik.android.core.lang.variable.assign
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.common.CSModel
import renetik.android.event.fire
import renetik.android.event.property.CSProperty.Companion.property
import renetik.android.event.property.CSSafeHasChangeValue
import renetik.android.event.property.CSSafeProperty.Companion.safe
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import renetik.android.testing.CSAssert.assert

@RunWith(RobolectricTestRunner::class)
class CSSafeHasChangeAndOrTest {

    @Test
    fun safeBooleanCombinationsHaveLogicalValues() {
        val parent = CSModel()
        val first = property(true).safe(parent)
        val second = property(false).safe(parent)
        val nullable = property<Boolean?>(null).safe(parent)

        val constantFirst: CSSafeHasChangeValue<Boolean> = false and first
        val constantSecond: CSSafeHasChangeValue<Boolean> = first and false
        val both: CSSafeHasChangeValue<Boolean> = first and second
        val nullableBoth: CSSafeHasChangeValue<Boolean> = first and nullable
        val either: CSSafeHasChangeValue<Boolean> = first or second

        assert(expected = false, actual = constantFirst.value)
        assert(expected = false, actual = constantSecond.value)
        assert(expected = false, actual = both.value)
        assert(expected = false, actual = nullableBoth.value)
        assert(expected = true, actual = either.value)

        second assign true
        nullable assign true
        assert(expected = true, actual = both.value)
        assert(expected = true, actual = nullableBoth.value)
    }

    @Test
    fun safeConditionalValuesTrackBothSources() {
        val parent = CSModel()
        val source = property(1).safe(parent)
        val condition = property(false).safe(parent)
        val whenTrue = source ifTrue condition
        val whenFalse = source ifFalse condition

        assert(expected = null, actual = whenTrue.value)
        assert(expected = 1, actual = whenFalse.value)

        condition assign true
        assert(expected = 1, actual = whenTrue.value)
        assert(expected = null, actual = whenFalse.value)

        source assign 2
        assert(expected = 2, actual = whenTrue.value)
    }

    @Test
    fun safeDelegateAndOrEventPreserveSafeChangeChannel() {
        val parent = CSModel()
        val source = property(2).safe(parent)
        val delegated = source.delegateValue(from = { it * 2 })
        val trigger = event()
        val sourceOrTrigger = source or trigger
        var delegatedUnsafe: Int? = null
        var triggeredUnsafe: Int? = null
        delegated.onUnsafeChange { delegatedUnsafe = it }
        sourceOrTrigger.onUnsafeChange { triggeredUnsafe = it }

        source assign 3
        assert(expected = 6, actual = delegated.value)
        assert(expected = 6, actual = delegatedUnsafe)

        trigger.fire()
        assert(expected = 3, actual = triggeredUnsafe)
    }

    @Test
    fun safePairDelegateRegistersOnlyWhileObserved() {
        val first = TrackingSafeValue(1)
        val second = TrackingSafeValue(2)
        val unary = first.delegateValue(from = { it * 2 })
        val delegated = (first to second).delegate(from = Int::plus)

        assert(expected = 2, actual = unary.value)
        assert(expected = 3, actual = delegated.value)
        assert(expected = 0, actual = first.changeRegistrations)
        assert(expected = 0, actual = first.unsafeChangeRegistrations)
        assert(expected = 0, actual = second.changeRegistrations)
        assert(expected = 0, actual = second.unsafeChangeRegistrations)

        val unsafeRegistration = delegated.onUnsafeChange { }
        assert(expected = 1, actual = first.unsafeChangeRegistrations)
        assert(expected = 1, actual = second.unsafeChangeRegistrations)

        val changeRegistration = delegated.onChange { }
        assert(expected = 1, actual = first.changeRegistrations)
        assert(expected = 1, actual = second.changeRegistrations)

        unsafeRegistration.cancel()
        changeRegistration.cancel()
        assert(expected = 0, actual = first.changeRegistrations)
        assert(expected = 0, actual = first.unsafeChangeRegistrations)
        assert(expected = 0, actual = second.changeRegistrations)
        assert(expected = 0, actual = second.unsafeChangeRegistrations)
    }

    @Test
    fun safePairDelegatePreservesBothChangeChannels() {
        val parent = CSModel()
        val first = property(1).safe(parent)
        val second = property(2).safe(parent)
        val delegated = (first to second).delegate(from = Int::plus)
        var unsafeValue: Int? = null
        var changeValue: Int? = null
        delegated.onUnsafeChange { unsafeValue = it }
        delegated.onChange { changeValue = it }

        Thread { first assign 3 }.apply {
            start()
            join()
        }

        assert(expected = 5, actual = delegated.value)
        assert(expected = 5, actual = unsafeValue)
        assert(expected = null, actual = changeValue)
        runUiThreadTasksIncludingDelayedTasks()
        assert(expected = 5, actual = changeValue)
    }

    private class TrackingSafeValue<T>(
        override val value: T,
    ) : CSSafeHasChangeValue<T> {
        var changeRegistrations = 0
        var unsafeChangeRegistrations = 0

        override fun onChange(function: (T) -> Unit): CSRegistration {
            changeRegistrations++
            return CSRegistration(isActive = true, onCancel = { changeRegistrations-- })
        }

        override fun onUnsafeChange(function: (T) -> Unit): CSRegistration {
            unsafeChangeRegistrations++
            return CSRegistration(isActive = true,
                onCancel = { unsafeChangeRegistrations-- })
        }
    }
}
