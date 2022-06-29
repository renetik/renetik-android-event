package renetik.android.event

import org.junit.Assert.assertEquals
import org.junit.Test
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.owner.CSEventOwnerHasDestroyBase
import renetik.android.event.owner.destroy

class CSEventOwnerTest {
	class TestOwner(parent: TestOwner? = null) : CSEventOwnerHasDestroyBase(parent) {
		val event = event<Int>()
		var eventValue: Int? = null

		init {
			register(event.listen { eventValue = it })
			register(parent?.event?.listen(event::fire))
		}
	}

	private val parent = TestOwner()
	private val parentChild = TestOwner(parent)
	private val parentChildChild = TestOwner(parentChild)

	@Test
	fun parentChildChildDestroy() {
		parent.event.fire(3)
		assertEquals(3, parentChildChild.eventValue)

		parentChildChild.destroy()
		parent.event.fire(5)
		assertEquals(5, parent.eventValue)
		assertEquals(5, parentChild.eventValue)
		assertEquals(3, parentChildChild.eventValue)
	}

	@Test
	fun parentChildDestroy() {
		parent.event.fire(3)
		assertEquals(3, parentChildChild.eventValue)

		parentChild.destroy()
		parent.event.fire(5)
		assertEquals(5, parent.eventValue)
		assertEquals(3, parentChild.eventValue)
		assertEquals(3, parentChildChild.eventValue)
	}

	@Test
	fun parentDestroy() {
		parent.event.fire(3)
		assertEquals(3, parentChildChild.eventValue)

		parent.destroy()
		parent.event.fire(5)
		assertEquals(3, parent.eventValue)
		assertEquals(3, parentChild.eventValue)
		assertEquals(3, parentChildChild.eventValue)
	}
}