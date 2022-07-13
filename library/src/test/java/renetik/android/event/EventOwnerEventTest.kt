package renetik.android.event

import org.junit.Assert.assertEquals
import org.junit.Test
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.common.CSModel
import renetik.android.event.common.destroy
import renetik.android.event.registration.register

/**
 * Event unregister after owner nulled
 */
class EventOwnerEventTest {
    @Test
    fun testUnregisteredAfterNilled() {
        val owner = CSModel()
        val event = event()
        var count = 0
        owner.register(event.listen { count += 1 })
        event.fire()
        event.fire()
        assertEquals(count, 2)
        owner.destroy()
        event.fire()
        assertEquals(count, 2)
    }

    @Test
    fun testOwnerDestroyed() {
        val owner = CSModel().apply { destroy() }
        val event = event()
        var count = 0
        owner.register(event.listen { count += 1 })
        event.fire()
        event.fire()
        assertEquals(count, 0)
    }
}
