package renetik.android.event

import org.junit.Assert.assertEquals
import org.junit.Test
import renetik.android.event.registrations.CSHasRegistrationsHasDestroyBase
import renetik.android.event.registrations.destroy
import renetik.android.event.registrations.register
import renetik.android.event.property.CSPropertyFunctions.property

/**
 * Event property unregister after owner nulled
 */
class EventOwnerPropertyTest {
    class SomeClass(parent: SomeClass? = null) : CSHasRegistrationsHasDestroyBase(parent) {
        val string = property("initial value")

        init {
            register(parent?.string?.onChange { string.value = it })
        }
    }

    @Test
    fun testUnregisteredAfterNilled() {
        val instance1 = SomeClass()
        val instance2 = SomeClass(instance1)
        val instance3 = SomeClass(instance2)
        assertEquals(instance3.string.value, "initial value")
        instance1.string.value = "first value"
        assertEquals(instance3.string.value, "first value")
        instance2.destroy()
        instance1.string.value = "second value"
        assertEquals(instance3.string.value, "first value")
    }
}