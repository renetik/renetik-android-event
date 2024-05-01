package renetik.android.event.registration

import org.junit.Test
import renetik.android.core.lang.value.CSValue
import renetik.android.core.lang.value.CSValue.Companion.value
import renetik.android.event.property.CSProperty
import renetik.android.event.property.CSProperty.Companion.property
import renetik.android.event.registration.CSHasChangeValue.Companion.delegate
import renetik.android.event.registration.CSHasChangeValue.Companion.delegateNullable
import renetik.android.event.registration.CSHasChangeValue.Companion.hasChangeValue
import renetik.android.event.registration.CSHasChangeValue.Companion.hasChangeValueNullable
import renetik.android.testing.CSAssert.assert

class CSHasChangeValueTest {
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
        var delegateChildValue1: Int? = null
        var delegateChildValue2: Int? = null
        delegateChild.onChange { delegateChildValue1 = it }
        delegateChild.action { delegateChildValue2 = it }
        assert(expected = null, actual = delegateChildValue1)
        assert(expected = null, actual = delegateChildValue2)
        property.value = value(property(7))
        assert(expected = 7, actual = delegateChildValue1)
        assert(expected = 7, actual = delegateChildValue2)
        property.value?.value?.value = 6
        assert(expected = 6, actual = delegateChildValue1)
        assert(expected = 6, actual = delegateChildValue2)
        property.value = null
        assert(expected = null, actual = delegateChildValue1)
        assert(expected = null, actual = delegateChildValue2)
    }
}