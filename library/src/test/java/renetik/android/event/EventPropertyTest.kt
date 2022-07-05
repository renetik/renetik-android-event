package renetik.android.event

import org.junit.Assert.assertEquals
import org.junit.Test
import renetik.android.event.property.CSEventPropertyFunctions.property
import renetik.android.event.property.apply
import renetik.android.event.property.onChange
import renetik.android.event.property.onChangeOnce
import renetik.android.event.registration.pause

/**
 * Simple event property use cases
 */
class EventPropertyTest {

    @Test
    fun testOnChange() {
        var count = 0
        var value by property("initial") { count += 1 }
        value = "second"
        value = "third"
        assertEquals(count, 2)
        assertEquals("third", value)
    }

    @Test
    fun testOnApply() {
        var count = 0
        var value by property("initial") { count += 1 }.apply()
        value = "second"
        value = "third"
        assertEquals(count, 3)
        assertEquals("third", value)
    }

    @Test
    fun testArgListen() {
        var count = 0
        var value by property(0) { count += 1 }
        value += 2
        value += 3
        assertEquals(5, value)
        assertEquals(2, count)
    }

    @Test
    fun testEquals() {
        var count = 0
        var value by property("") { count += 1 }
        value = "second"
        value = "second"
        assertEquals(count, 1)
        assertEquals("second", value)
    }

    @Test
    fun testNotFireAndOnChangeOnce() {
        var count = 0
        val property = property("")
        property.onChangeOnce { count += 1 }
        property.value("one", fire = false)
        property.value = "two"
        property.value = "three"
        assertEquals(count, 1)
        assertEquals("three", property.value)
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