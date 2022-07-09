package renetik.android.event

import org.junit.Assert.assertEquals
import org.junit.Test
import org.robolectric.shadows.ShadowLooper
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.owner.CSEventOwnerHasDestroyBase
import renetik.android.event.owner.destroy
import renetik.android.event.owner.register

/**
 * Event unregister after owner nulled
 */
class EventOwnerEventTest {
    @Test
    fun testUnregisteredAfterNilled() {
        val owner = CSEventOwnerHasDestroyBase()
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
}
