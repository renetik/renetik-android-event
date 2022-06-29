package renetik.android.event

import org.junit.Assert.assertEquals
import org.junit.Test
import renetik.android.event.CSEvent.Companion.event

class CSEventTest {

	private var eventCounter = 0
	private var eventValue: String? = ""
	private val event = event<String>()

	@Test
	fun fireTwiceAndCancel() {
		event.add { registration, value ->
			eventCounter++
			eventValue = value
			if (eventCounter == 2) registration.cancel()
		}
		event.fire("testOne")
		event.fire("testTwo")
		event.fire("testThree")
		assertEquals(2, eventCounter)
		assertEquals("testTwo", eventValue)
	}

	@Test
	fun twoListenersCancelBothInSecond() {
		val eventOneRegistration = event.add { _, value ->
			eventCounter++
			eventValue = value
		}
		event.add { registration, value ->
			eventCounter++
			eventValue = value
			registration.cancel()
			eventOneRegistration.cancel()
		}
		event.fire("testOne")
		assertEquals(2, eventCounter)
		assertEquals("testOne", eventValue)

		event.fire("testTwo")
		assertEquals(2, eventCounter)
		assertEquals("testOne", eventValue)
	}

	@Test
	fun twoListenersAddSecondWhileRunning() {
		event.add { eventOneRegistration, value1 ->
			eventCounter++
			eventValue = value1
			if (eventCounter == 1)
				event.add { registration, value2 ->
					eventCounter++
					eventValue = value2
					registration.cancel()
					eventOneRegistration.cancel()
				}
		}

		event.fire("testOne")
		assertEquals(1, eventCounter)
		assertEquals("testOne", eventValue)

		event.fire("testTwo")
		assertEquals(3, eventCounter)
		assertEquals("testTwo", eventValue)

		event.fire("testThree")
		assertEquals(3, eventCounter)
		assertEquals("testTwo", eventValue)
	}
}


