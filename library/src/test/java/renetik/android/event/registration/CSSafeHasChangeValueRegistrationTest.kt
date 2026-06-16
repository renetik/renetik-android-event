package renetik.android.event.registration

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks
import renetik.android.core.lang.tuples.to
import renetik.android.core.lang.variable.assign
import renetik.android.event.common.CSModel
import renetik.android.event.property.CSProperty.Companion.property
import renetik.android.event.property.CSSafeProperty.Companion.safe
import renetik.android.event.registration.hasChangeValue
import renetik.android.event.registration.onChange
import renetik.android.testing.CSAssert.assert

@RunWith(RobolectricTestRunner::class)
class CSSafeHasChangeValueRegistrationTest {

    @Test
    fun normalValueAndSafeBooleanKeepsUnsafePropagation() {
        val parent = CSModel()
        val source = property(1)
        val gate = property(false).safe(parent)
        val gated = source and gate
        var unsafeValue: Int? = null
        var safeValue: Int? = null
        gated.onUnsafeChange { unsafeValue = it }
        gated.onChange { safeValue = it }

        source assign 2
        assert(expected = 2, actual = gated.value)
        assert(expected = null, actual = unsafeValue)
        assert(expected = null, actual = safeValue)

        Thread { gate assign true }.apply {
            start()
            join()
        }
        assert(expected = 2, actual = gated.value)
        assert(expected = 2, actual = unsafeValue)
        assert(expected = null, actual = safeValue)
        runUiThreadTasksIncludingDelayedTasks()
        assert(expected = 2, actual = safeValue)
    }

    @Test
    fun safeIdentityHasChangeValueKeepsUnsafePropagation() {
        val parent = CSModel()
        val source = property(1).safe(parent)
        val identity = source.hasChangeValue(parent)
        var unsafeValue: Int? = null
        var safeValue: Int? = null
        identity.onUnsafeChange { unsafeValue = it }
        identity.onChange { safeValue = it }

        Thread { source assign 2 }.apply {
            start()
            join()
        }
        assert(expected = 2, actual = identity.value)
        assert(expected = 2, actual = unsafeValue)
        assert(expected = null, actual = safeValue)
        runUiThreadTasksIncludingDelayedTasks()
        assert(expected = 2, actual = safeValue)
    }

    @Test
    fun safeFifthTupleOnChangeUsesUnsafePropagation() {
        val parent = CSModel()
        val item1 = property(1)
        val item2 = property(2)
        val item3 = property(3)
        val item4 = property(4)
        val item5 = property(5).safe(parent)
        var value: Int? = null

        (item1 to item2 to item3 to item4 to item5).onChange { first, second, third, fourth, fifth ->
            value = first + second + third + fourth + fifth
        }
        Thread { item5 assign 6 }.apply {
            start()
            join()
        }
        assert(expected = 16, actual = value)
    }

    @Test
    fun safeFifthTupleHasChangeValueFromKeepsUnsafePropagation() {
        val parent = CSModel()
        val item1 = property(1)
        val item2 = property(2)
        val item3 = property(3)
        val item4 = property(4)
        val item5 = property(5).safe(parent)
        val combined = (item1 to item2 to item3 to item4 to item5)
            .hasChangeValue(parent, from = { first, second, third, fourth, fifth ->
                first + second + third + fourth + fifth
            })
        var unsafeValue: Int? = null
        var safeValue: Int? = null
        combined.onUnsafeChange { unsafeValue = it }
        combined.onChange { safeValue = it }

        Thread { item5 assign 6 }.apply {
            start()
            join()
        }
        assert(expected = 16, actual = combined.value)
        assert(expected = 16, actual = unsafeValue)
        assert(expected = null, actual = safeValue)
        runUiThreadTasksIncludingDelayedTasks()
        assert(expected = 16, actual = safeValue)
    }
}
