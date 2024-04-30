package renetik.android.event.registration

import org.junit.Test
import renetik.android.core.lang.value.CSValue
import renetik.android.core.lang.value.CSValue.Companion.value
import renetik.android.event.property.CSProperty
import renetik.android.event.property.CSProperty.Companion.property
import renetik.android.event.registration.CSHasChangeValue.Companion.delegateChild
import renetik.android.event.registration.CSHasChangeValue.Companion.delegateNullableChild
import renetik.android.testing.CSAssert.assert

class CSHasChangeValueTest {
    @Test
    fun delegateChild() {
        val property1 = property<CSValue<CSProperty<Int>>>(value(property(5)))
        val delegateChild: CSHasChangeValue<Int> =
            property1.delegateChild(child = { it.value })
        var delegateChildValue1: Int? = null
        var delegateChildValue2: Int? = null
        delegateChild.onChange { delegateChildValue1 = it }
        delegateChild.action { delegateChildValue2 = it }
        assert(expected = null, actual = delegateChildValue1)
        assert(expected = 5, actual = delegateChildValue2)
        property1.value.value.value = 6
        assert(expected = 6, actual = delegateChildValue1)
        assert(expected = 6, actual = delegateChildValue2)
        property1.value = value(property(7))
        assert(expected = 7, actual = delegateChildValue1)
        assert(expected = 7, actual = delegateChildValue2)
    }

    @Test
    fun delegateNullableChild() {
        val property = property<CSValue<CSProperty<Int>>?>(null)
        val delegateChild: CSHasChangeValue<Int?> =
            property.delegateNullableChild(child = { it?.value })
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
