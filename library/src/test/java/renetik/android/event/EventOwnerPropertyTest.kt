package renetik.android.event

import org.junit.Assert.assertEquals
import org.junit.Test
import renetik.android.event.common.CSModel
import renetik.android.event.common.destroy
import renetik.android.event.property.CSProperty.Companion.property
import renetik.android.event.registration.register

/**
 * Event property unregister after owner nulled
 */
class EventOwnerPropertyTest {
    class SomeClass(parent: SomeClass? = null) : CSModel(parent) {
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
        assertEquals("initial value", instance3.string.value)
        instance1.string.value = "first value"
        assertEquals("first value", instance3.string.value)
        instance2.destroy()
        instance1.string.value = "second value"
        assertEquals("first value", instance3.string.value)
    }
}