package renetik.android.event

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import renetik.android.event.property.CSProperty.Companion.lateProperty

/**
 * Simple event property use cases
 */
class LateEventPropertyTest {

    @Test
    fun testOnChange() {
        var count = 0
        var value: String by lateProperty { count += 1 }
        assertThrows(Exception::class.java) { print(value) }
        value = "first"
        value = "second"
        assertEquals(count, 2)
        assertEquals("second", value)
    }

//    TODO: how this should work ?
//    @Test
//    fun testOnApply() {
//        var count = 0
//        var value by lateProperty<String> { count += 1 }.apply()
//        value = "second"
//        value = "third"
//        assertEquals(count, 3)
//        assertEquals("third", value)
//    }
}