package renetik.android.event

import org.junit.Assert.assertEquals
import org.junit.Test
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.registration.pause

/**
 * Simple event use cases
 */
class EventTest {
    @Test
    fun testListen() {
        val event = event()
        var count = 0
        event.listen { count += 1 }
        event.fire()
        event.fire()
        assertEquals(count, 2)
    }

    @Test
    fun testArgListen() {
        val event = event<Int>()
        var count = 0
        event.listen { count += it }
        event.fire(2)
        event.fire(3)
        assertEquals(count, 5)
    }

    @Test
    fun testListenOnce() {
        val event = event()
        var count = 0
        event.listenOnce { count += 1 }
        event.fire()
        event.fire()
        assertEquals(count, 1)
    }

    @Test
    fun testArgListenOnce() {
        val event = event()
        var count = 0
        event.listenOnce { count += 1 }
        event.fire()
        event.fire()
        assertEquals(count, 1)
    }

    @Test
    fun testEventCancel() {
        val event = event()
        var count = 0
        event.listen { registration, _ ->
            count += 1
            if (count == 2) registration.cancel()
        }
        event.fire()
        event.fire()
        event.fire()
        assertEquals(count, 2)
    }

    @Test
    fun testStringEventCancel() {
        val event = event<String>()
        var value: String? = null
        event.listen { registration, newValue ->
            if (newValue == "second") registration.cancel()
            value = newValue
        }
        event.fire("first")
        assertEquals("first", value)
        event.fire("second")
        assertEquals("second", value)
        event.fire("third")
        assertEquals("second", value)
    }

    @Test
    fun testEventPause() {
        val event = event()
        var count = 0
        val registration = event.listen { count += 1 }
        registration.pause { event.fire() }
        assertEquals(count, 0)
        event.fire()
        assertEquals(count, 1)
    }
}


