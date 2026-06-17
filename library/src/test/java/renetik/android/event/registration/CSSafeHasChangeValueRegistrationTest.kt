package renetik.android.event.registration

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks
import renetik.android.core.lang.tuples.CSQuadruple
import renetik.android.core.lang.tuples.CSSixtuple
import renetik.android.core.lang.tuples.to
import renetik.android.core.lang.variable.assign
import renetik.android.event.common.CSModel
import renetik.android.event.property.CSProperty.Companion.property
import renetik.android.event.property.CSSafeProperty.Companion.safe
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

        (item1 to item2 to item3 to item4 to item5).onUnsafeChange { first, second, third, fourth, fifth ->
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
            .hasChangeValue(parent, unsafeFrom = { first, second, third, fourth, fifth ->
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

    @Test
    fun safeLastTupleHasChangeValueIdentityKeepsUnsafePropagation() {
        val parent = CSModel()
        val item1 = property(1)
        val item2 = property(2)
        val item3 = property(3).safe(parent)
        val item4 = property(4).safe(parent)
        val item5 = property(5)
        val item6 = property(6).safe(parent)

        val safeThird = (item1 to item2 to item3).hasChangeValue(parent)
        val safeFourth = (item1 to item2 to item5 to item4).hasChangeValue(parent)
        val safeSixth = (item1 to item2 to item3 to item4 to item5 to item6)
            .hasChangeValue(parent)
        var safeThirdUnsafe: Triple<Int, Int, Int>? = null
        var safeFourthUnsafe: CSQuadruple<Int, Int, Int, Int>? = null
        var safeSixthUnsafe: CSSixtuple<Int, Int, Int, Int, Int, Int>? = null
        var safeSixthSafe: CSSixtuple<Int, Int, Int, Int, Int, Int>? = null
        safeThird.onUnsafeChange { safeThirdUnsafe = it }
        safeFourth.onUnsafeChange { safeFourthUnsafe = it }
        safeSixth.onUnsafeChange { safeSixthUnsafe = it }
        safeSixth.onChange { safeSixthSafe = it }

        Thread { item3 assign 4; item4 assign 5; item6 assign 7 }.apply {
            start()
            join()
        }
        assert(expected = 4, actual = safeThird.value.third)
        assert(expected = 4, actual = safeThirdUnsafe?.third)
        assert(expected = 5, actual = safeFourth.value.fourth)
        assert(expected = 5, actual = safeFourthUnsafe?.fourth)
        assert(expected = 7, actual = safeSixth.value.sixth)
        assert(expected = 7, actual = safeSixthUnsafe?.sixth)
        assert(expected = null, actual = safeSixthSafe)
        runUiThreadTasksIncludingDelayedTasks()
        assert(expected = 7, actual = safeSixthSafe?.sixth)
    }

    @Test
    fun safeLastTupleHasChangeValueFromKeepsUnsafePropagation() {
        val parent = CSModel()
        val item1 = property(1)
        val item2 = property(2)
        val item3 = property(3).safe(parent)
        val item4 = property(4).safe(parent)
        val item5 = property(5)
        val item6 = property(6).safe(parent)
        val safeSecond = (item1 to item3)
            .hasChangeValue(parent, unsafeFrom = { first, second -> first + second })
        val safeThird = (item1 to item2 to item3)
            .hasChangeValue(parent, unsafeFrom = { first, second, third ->
                first + second + third
            })
        val safeFourth = (item1 to item2 to item5 to item4)
            .hasChangeValue(parent, unsafeFrom = { first, second, third, fourth ->
                first + second + third + fourth
            })
        val safeSixth = (item1 to item2 to item3 to item4 to item5 to item6)
            .hasChangeValue(parent, unsafeFrom = { first, second, third, fourth, fifth, sixth ->
                first + second + third + fourth + fifth + sixth
            })
        var safeSecondUnsafe: Int? = null
        var safeThirdUnsafe: Int? = null
        var safeFourthUnsafe: Int? = null
        var safeSixthUnsafe: Int? = null
        safeSecond.onUnsafeChange { safeSecondUnsafe = it }
        safeThird.onUnsafeChange { safeThirdUnsafe = it }
        safeFourth.onUnsafeChange { safeFourthUnsafe = it }
        safeSixth.onUnsafeChange { safeSixthUnsafe = it }

        Thread { item3 assign 4; item4 assign 5; item6 assign 7 }.apply {
            start()
            join()
        }
        assert(expected = 5, actual = safeSecond.value)
        assert(expected = 5, actual = safeSecondUnsafe)
        assert(expected = 7, actual = safeThird.value)
        assert(expected = 7, actual = safeThirdUnsafe)
        assert(expected = 13, actual = safeFourth.value)
        assert(expected = 13, actual = safeFourthUnsafe)
        // item6 is the safe last item, so it propagates synchronously. item3 and
        // item4 are safe but not last, so they propagate on the main thread like
        // any other onChange source: their new values are not applied yet here.
        assert(expected = 22, actual = safeSixth.value)
        assert(expected = 22, actual = safeSixthUnsafe)
        runUiThreadTasksIncludingDelayedTasks()
        assert(expected = 24, actual = safeSixth.value)
        assert(expected = 24, actual = safeSixthUnsafe)
    }
}
