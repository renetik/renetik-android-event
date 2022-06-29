package renetik.android.event

import org.junit.Assert.assertEquals
import org.junit.Test
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.owner.CSEventOwnerHasDestroyBase
import renetik.android.event.owner.destroy
import renetik.android.event.property.CSEventPropertyFunctions.property

class CSEventPropertyOwnerTest {
	private class TestOwner(parent: TestOwner? = null) : CSEventOwnerHasDestroyBase(parent) {
		val property = property(0)

		init {
			register(parent?.property?.onChange { property.value = it })
		}
	}

	private val parent = TestOwner()
	private val parentChild = TestOwner(parent)
	private val parentChildChild = TestOwner(parentChild)

	@Test
	fun parentChildChildDestroy() {
		parent.property.value = 3
		assertEquals(3, parentChildChild.property.value)

		parentChildChild.destroy()
		parent.property.value = 5
		assertEquals(5, parent.property.value)
		assertEquals(5, parentChild.property.value)
		assertEquals(3, parentChildChild.property.value)
	}

	@Test
	fun parentDestroy() {
		parent.property.value = 3
		assertEquals(3, parentChildChild.property.value)

		parent.destroy()
		parent.property.value = 5
		assertEquals(5, parent.property.value)
		assertEquals(3, parentChild.property.value)
		assertEquals(3, parentChildChild.property.value)
	}
}