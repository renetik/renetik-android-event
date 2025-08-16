package renetik.android.common

import org.junit.Assert.assertEquals
import org.junit.Test
import renetik.android.event.common.CSModel
import renetik.android.event.common.destruct
import renetik.android.event.registration.invoke

class CSModelTest {
    @Test
    fun testChildDestruct() {
        val parent = CSModel()
        assertEquals(0, parent.registrations.size)
        val child = CSModel(parent)
        var isDestructedCount = 0
        child.eventDestruct { isDestructedCount += 1 }
        assertEquals(0, parent.registrations.size)
        assertEquals(1, child.registrations.size)
        child.destruct()
        assertEquals(0, parent.registrations.size)
        assertEquals(0, child.registrations.size)
        assertEquals(true, child.isDestructed)
        assertEquals(1, isDestructedCount)
    }

    @Test
    fun testParentDestruct() {
        val parent = CSModel()
        assertEquals(0, parent.registrations.size)
        val child = CSModel(parent)
        var isDestructedCount = 0
        child.eventDestruct { isDestructedCount += 1 }
        assertEquals(0, parent.registrations.size)
        assertEquals(1, child.registrations.size)
        parent.destruct()
        assertEquals(0, parent.registrations.size)
        assertEquals(0, child.registrations.size)
        assertEquals(true, parent.isDestructed)
        assertEquals(true, child.isDestructed)
        assertEquals(1, isDestructedCount)
    }
}
