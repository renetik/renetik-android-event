package renetik.android.event

import org.junit.Assert.assertEquals
import org.junit.Test
import renetik.android.event.property.CSEventPropertyFunctions.property
import renetik.android.event.property.onChange
import renetik.android.event.property.onChangeOnce
import renetik.android.event.registration.pause

/**
 * Simple event property use cases
 */
class EventPropertyTest {

    @Test
    fun testOnChange() {
        val property = property("initial")
        var count = 0
        property.onChange { count += 1 }
        property.value = "second"
        property.value = "third"
        assertEquals(count, 2)
        assertEquals("third", property.value)
    }

    @Test
    fun testOnApply() {
        var count = 0
        val property = property("initial") { count += 1 }.apply()
        property.value = "second"
        property.value = "third"
        assertEquals(count, 3)
        assertEquals("third", property.value)
    }

    @Test
    fun testArgListen() {
        var count = 0
        val property = property(0) { count += 1 }
        property.value += 2
        property.value += 3
        assertEquals(5, property.value)
        assertEquals(2, count)
    }

    @Test
    fun testEquals() {
        var count = 0
        val property = property("") { count += 1 }
        property.value = "second"
        property.value = "second"
        assertEquals(count, 1)
        assertEquals("second", property.value)
    }

    @Test
    fun testOnChangeOnce() {
        var count = 0
        val property = property("")
        property.onChangeOnce { count += 1 }
        property.value = "second"
        property.value = "third"
        assertEquals(count, 1)
        assertEquals("third", property.value)
    }

    @Test
    fun testEventCancel() {
        var count = 0
        val property = property(0)
        property.onChange { registration, value ->
            count += value
            if (count > 2) registration.cancel()
        }
        property.value = 1
        property.value = 2
        property.value = 3
        assertEquals(count, 3)
    }

    @Test
    fun testEventPause() {
        var count = 0
        val property = property(0)
        val registration = property.onChange { count += it }
        registration.pause { property.value = 1 }
        assertEquals(count, 0)
        property.value = 2
        assertEquals(count, 2)
    }
}