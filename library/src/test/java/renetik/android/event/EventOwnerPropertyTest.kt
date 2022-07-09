package renetik.android.event

import org.junit.Assert.assertEquals
import org.junit.Test
import renetik.android.event.owner.CSEventOwnerHasDestroyBase
import renetik.android.event.owner.destroy
import renetik.android.event.owner.register
import renetik.android.event.property.CSEventPropertyFunctions.property

/**
 * Event property unregister after owner nulled
 */
class EventOwnerPropertyTest {
    class SomeClass(parent: SomeClass? = null) : CSEventOwnerHasDestroyBase(parent) {
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