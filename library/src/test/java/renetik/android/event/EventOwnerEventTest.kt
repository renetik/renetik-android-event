package renetik.android.event

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import renetik.android.core.logging.CSDummyLogger
import renetik.android.core.logging.CSLog.init
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.common.CSModel
import renetik.android.event.common.destruct
import renetik.android.event.registration.plus

@RunWith(RobolectricTestRunner::class)
class EventOwnerEventTest {

    @Before
    fun before() = init(logger = CSDummyLogger())

    @Test
    fun testUnregisteredAfterNilled() {
        val owner = CSModel()
        val event = event()
        var count = 0
        owner + event.listen { count += 1 }
        event()
        event()
        assertEquals(2, count)
        owner.destruct()
        event()
        assertEquals(2, count)
    }

    @Test
    fun testOwnerDestroyed() {
        val owner = CSModel().apply { destruct() }
        val event = event()
        var count = 0
        owner + event.listen { count += 1 }
        event()
        event()
        assertEquals(0, count)
    }
}
