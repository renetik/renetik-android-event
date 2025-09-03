package renetik.android.event.registration

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import renetik.android.core.lang.value.CSValue
import renetik.android.core.lang.value.CSValue.Companion.value
import renetik.android.core.lang.variable.assign
import renetik.android.core.lang.variable.plusAssign
import renetik.android.core.lang.variable.setFalse
import renetik.android.core.lang.variable.setTrue
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.fire
import renetik.android.event.property.CSProperty
import renetik.android.event.property.CSProperty.Companion.property
import renetik.android.event.registration.CSHasChangeValue.Companion.delegate
import renetik.android.event.registration.CSHasChangeValue.Companion.delegateIsChange
import renetik.android.event.registration.CSHasChangeValue.Companion.delegateNullable
import renetik.android.event.registration.CSHasChangeValue.Companion.hasChangeValue
import renetik.android.event.registration.CSHasChangeValue.Companion.hasChangeValueNullable
import renetik.android.event.registration.CSHasChangeValue.Companion.onChange
import renetik.android.testing.CSAssert.assert

@RunWith(RobolectricTestRunner::class)
class CSHasChangeValueTest {

    @Test
    fun delegatePropertyOrEvent() {
        val property = property(0)
        val event = event()
        val propertyOrEvent: CSHasChangeValue<Int> = property or event
        var propertyOrEventChangeCount = 0
        propertyOrEvent.onChange { propertyOrEventChangeCount += 1 }
        property assign 1
        assert(expected = 1, actual = propertyOrEvent.value)
        assert(expected = 1, actual = propertyOrEventChangeCount)
        event.fire()
        assert(expected = 1, actual = propertyOrEvent.value)
        assert(expected = 2, actual = propertyOrEventChangeCount)
    }

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
    fun delegateIsChange() {
        val property1 = property(0)
        val property2 = property(0)
        val delegateIsChange = property1.delegateIsChange()
        var _isChange: Boolean? = null
        var _value2: Int? = null
        (delegateIsChange to property2).onChange { isChange, value2 ->
            _isChange = isChange
            _value2 = value2
        }
        assert(expected = null, actual = _isChange)
        property2 assign 1
        assert(expected = false, actual = _isChange)
        assert(expected = 1, actual = _value2)
        assert(expected = 0, actual = property1.value)
        assert(expected = 1, actual = property2.value)
        property1 assign 1
        assert(expected = true, actual = _isChange)
        assert(expected = 1, actual = _value2)
        assert(expected = 1, actual = property1.value)
        assert(expected = 1, actual = property2.value)
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

    class SampleData(val value: Int? = null) {
        val bpm: CSProperty<Int?> = property(value)
    }

    @Test
    fun delegateNullableChild2() {
        val sampleProperty = property<SampleData?>(null)
        val bpmProperty = sampleProperty.delegateNullable(child = { it?.bpm })
        var bpmPropertyOnChangeValue: Int? = null
        var onChangeCount = 0
        val onChangeRegistration = bpmProperty.onChange {
            bpmPropertyOnChangeValue = it
            onChangeCount += 1
        }
        var bpmPropertyActionValue: Int? = null
        var actionCount = 0
        bpmProperty.action {
            bpmPropertyActionValue = it
            actionCount += 1
        }
        assert(expected = null, actual = bpmProperty.value)
        assert(expected = null, actual = bpmPropertyOnChangeValue)
        assert(expected = null, actual = bpmPropertyActionValue)
        assert(expected = 0, actual = onChangeCount)
        assert(expected = 1, actual = actionCount)

        onChangeRegistration.paused { sampleProperty assign SampleData() }
        assert(expected = null, actual = bpmProperty.value)
        assert(expected = null, actual = bpmPropertyOnChangeValue)
        assert(expected = null, actual = bpmPropertyActionValue)
        assert(expected = 0, actual = onChangeCount)
        assert(expected = 1, actual = actionCount)

        sampleProperty.value?.bpm?.value = 6
        assert(expected = 6, actual = bpmProperty.value)
        assert(expected = 6, actual = bpmPropertyOnChangeValue)
        assert(expected = 6, actual = bpmPropertyActionValue)
        assert(expected = 1, actual = onChangeCount)
        assert(expected = 2, actual = actionCount)

        onChangeRegistration.paused {
            sampleProperty.value?.bpm?.value = 7
        }
        assert(expected = 7, actual = bpmProperty.value)
        assert(expected = 6, actual = bpmPropertyOnChangeValue)
        assert(expected = 7, actual = bpmPropertyActionValue)
        assert(expected = 1, actual = onChangeCount)
        assert(expected = 3, actual = actionCount)

        onChangeRegistration.paused {
            sampleProperty.value = SampleData(100)
        }
        assert(expected = 100, actual = bpmProperty.value)
        assert(expected = 6, actual = bpmPropertyOnChangeValue)
        assert(expected = 100, actual = bpmPropertyActionValue)
        assert(expected = 1, actual = onChangeCount)
        assert(expected = 4, actual = actionCount)

        onChangeRegistration.paused {
            sampleProperty.value = SampleData(200)
            sampleProperty.value?.bpm?.value = 7
        }
        assert(expected = 7, actual = bpmProperty.value)
        assert(expected = 6, actual = bpmPropertyOnChangeValue)
        assert(expected = 7, actual = bpmPropertyActionValue)
        assert(expected = 1, actual = onChangeCount)
        assert(expected = 6, actual = actionCount)
    }

    @Test
    fun hasChangeValueNullableChild() {
        val property = property<CSValue<CSProperty<Int>>?>(null)
        val delegateChild = property.hasChangeValueNullable(child = { it?.value })
        testDelegateNullableChildProperty(property, delegateChild)
    }

    @Test
    fun hasChangeValueBooleansAnd() {
        val propertyFirst = property(false)
        val propertySecond = property(false)
        var trueAndTrue = 0
        (propertyFirst and propertySecond).onTrue { trueAndTrue += 1 }
        var falseAndTrue = 0
        (!propertyFirst and propertySecond).onTrue { falseAndTrue += 1 }
        var trueAndFalse = 0
        (propertyFirst and !propertySecond).onTrue { trueAndFalse += 1 }
        var falseAndFalse = 0
        (!propertyFirst and !propertySecond).onTrue { falseAndFalse += 1 }
        assert("0,0,0,0", "$trueAndTrue,$falseAndTrue,$trueAndFalse,$falseAndFalse")
        propertyFirst.setTrue()
        assert("0,0,1,0", "$trueAndTrue,$falseAndTrue,$trueAndFalse,$falseAndFalse")
        propertyFirst.setFalse()
        propertySecond.setTrue()
        assert("0,1,1,1", "$trueAndTrue,$falseAndTrue,$trueAndFalse,$falseAndFalse")
        propertyFirst.setTrue()
        assert("1,1,1,1", "$trueAndTrue,$falseAndTrue,$trueAndFalse,$falseAndFalse")
    }


    @Test
    fun hasChangeValueBooleansAndOthers() {
        val isRecording = property(true)
        val duration: CSProperty<Int> = property(0)
        val isRecorded = duration.hasChangeValue(from = { it >= 500 })
        var isRecordedOnChange = 0
        isRecorded.onChange { isRecordedOnChange += 1 }
        var isNotRecordingAndRecorded = 0
        (!isRecording and isRecorded).onTrue { isNotRecordingAndRecorded += 1 }
        assert(expected = 0, isNotRecordingAndRecorded)
        isRecording.setFalse()
        assert(expected = 0, isNotRecordingAndRecorded)
        duration.value = 1000
        assert(expected = false, isRecording.value)
        assert(expected = true, isRecorded.value)
        assert(expected = 1, isRecordedOnChange)
        assert(expected = 1, isNotRecordingAndRecorded)
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
        property assign value(property(7))
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
        property assign null
        assert(expected = null, actual = delegateChildOnChangeValue)
        assert(expected = null, actual = delegateChildActionValue)
    }

    @Test
    fun delegateEventIsNull() {
        val property = property<Int?>(null)
        var eventIsNullCount = 0
        property.eventIsNull.onChange { eventIsNullCount += 1 }
        var eventIsNotNullCount = 0
        property.eventIsNotNull.onChange { eventIsNotNullCount += 1 }
        var eventIsNotNullCount2 = 0
        property.eventIsNotNull().onChange { eventIsNotNullCount2 += 1 }

        property assign 5
        assert(expected = 0, actual = eventIsNullCount)
        assert(expected = 1, actual = eventIsNotNullCount)
        assert(expected = 1, actual = eventIsNotNullCount2)
        property assign 6
        assert(expected = 0, actual = eventIsNullCount)
        assert(expected = 1, actual = eventIsNotNullCount)
        assert(expected = 1, actual = eventIsNotNullCount2)
        property assign null
        assert(expected = 1, actual = eventIsNullCount)
        assert(expected = 1, actual = eventIsNotNullCount)
        assert(expected = 1, actual = eventIsNotNullCount2)
        property assign 5
        assert(expected = 1, actual = eventIsNullCount)
        assert(expected = 2, actual = eventIsNotNullCount)
        assert(expected = 2, actual = eventIsNotNullCount2)
    }

    @Test
    fun testPairDelegateFrom() {
        val propertyInt = property(0)
        val propertyBool = property(false)
        val pair = propertyInt to propertyBool
        var invocationCount = 0
        val pairDelegate = pair.delegate(from = { int, bool ->
            invocationCount += 1
            "$int-$bool"
        })
        assert(0, invocationCount)
        assert("0-false", pairDelegate.value)
        assert(1, invocationCount)
        propertyInt assign 1
        assert("1-false", pairDelegate.value)
        assert(2, invocationCount)
        propertyBool assign true
        assert("1-true", pairDelegate.value)
        assert(3, invocationCount)
    }
}
