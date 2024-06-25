package renetik.android.event.registration

import org.junit.Test
import renetik.android.core.lang.value.CSValue
import renetik.android.core.lang.value.CSValue.Companion.value
import renetik.android.core.lang.variable.plusAssign
import renetik.android.event.property.CSProperty
import renetik.android.event.property.CSProperty.Companion.property
import renetik.android.event.registration.CSHasChangeValue.Companion.delegate
import renetik.android.event.registration.CSHasChangeValue.Companion.delegateNullable
import renetik.android.event.registration.CSHasChangeValue.Companion.hasChangeValue
import renetik.android.event.registration.CSHasChangeValue.Companion.hasChangeValueNullable
import renetik.android.testing.CSAssert.assert

class CSHasChangeValueTest {
    @Test
    fun delegate() {
        val property = property(0)
        val isRecorded = property.delegate(from = { it > 1 })
        val isRecordedUser1 = isRecorded.hasChangeValue(from = { "$it" })
        val isRecordedUser2 = isRecorded.hasChangeValue(from = { "$it" })
        assert(expected = false, actual = isRecorded.value)
        assert(expected = "false", actual = isRecordedUser1.value)
        assert(expected = "false", actual = isRecordedUser2.value)
        property += 5
        assert(expected = true, actual = isRecorded.value)
        assert(expected = "true", actual = isRecordedUser1.value)
        assert(expected = "true", actual = isRecordedUser2.value)
    }

    @Test
    fun delegateChild() {
        val property = property<CSValue<CSProperty<Int>>>(value(property(5)))
        val delegateChild = property.delegate(child = { it.value })
        testDelegateChildProperty(property, delegateChild)
    }

    @Test
    fun hasChangeValueChild() {
        val property = property<CSValue<CSProperty<Int>>>(value(property(5)))
        val delegateChild = property.hasChangeValue(child = { it.value })
        testDelegateChildProperty(property, delegateChild)
    }

    private fun testDelegateChildProperty(
        delegatedProperty: CSProperty<CSValue<CSProperty<Int>>>,
        testedProperty: CSHasChangeValue<Int>
    ) {
        var delegateChildValue1: Int? = null
        var delegateChildValue2: Int? = null
        testedProperty.onChange { delegateChildValue1 = it }
        testedProperty.action { delegateChildValue2 = it }
        assert(expected = null, actual = delegateChildValue1)
        assert(expected = 5, actual = delegateChildValue2)
        delegatedProperty.value.value.value = 6
        assert(expected = 6, actual = delegateChildValue1)
        assert(expected = 6, actual = delegateChildValue2)
        delegatedProperty.value = value(property(7))
        assert(expected = 7, actual = delegateChildValue1)
        assert(expected = 7, actual = delegateChildValue2)
    }

    @Test
    fun delegateNullableChild() {
        val property = property<CSValue<CSProperty<Int>>?>(null)
        val delegateChild = property.delegateNullable(child = { it?.value })
        testDelegateNullableChildProperty(property, delegateChild)
    }

    @Test
    fun hasChangeValueNullableChild() {
        val property = property<CSValue<CSProperty<Int>>?>(null)
        val delegateChild = property.hasChangeValueNullable(child = { it?.value })
        testDelegateNullableChildProperty(property, delegateChild)
    }

    private fun testDelegateNullableChildProperty(
        property: CSProperty<CSValue<CSProperty<Int>>?>,
        delegateChild: CSHasChangeValue<Int?>
    ) {
        var delegateChildOnChangeValue: Int? = null
        var delegateChildActionValue: Int? = null
        val delegateChildOnChange = delegateChild.onChange { delegateChildOnChangeValue = it }
        delegateChild.action { delegateChildActionValue = it }
        assert(expected = null, actual = delegateChildOnChangeValue)
        assert(expected = null, actual = delegateChildActionValue)
        property.value = value(property(7))
        assert(expected = 7, actual = delegateChildOnChangeValue)
        assert(expected = 7, actual = delegateChildActionValue)
        property.value?.value?.value = 6
        assert(expected = 6, actual = delegateChildOnChangeValue)
        assert(expected = 6, actual = delegateChildActionValue)
        delegateChildOnChange.pause()
        property.value?.value?.value = 5
        assert(expected = 6, actual = delegateChildOnChangeValue)
        assert(expected = 5, actual = delegateChildActionValue)
        delegateChildOnChange.resume()
        property.value = null
        assert(expected = null, actual = delegateChildOnChangeValue)
        assert(expected = null, actual = delegateChildActionValue)
    }
}
