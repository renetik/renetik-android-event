package renetik.android.event

import org.junit.Assert.assertEquals
import org.junit.Test
import renetik.android.event.CSEvent.Companion.event

class CSEventTest {

	private var count = 0
	private var eventValue: String? = ""
	private val event = event<String>()

	@Test
	fun onSecondEventCancel() {
		event.listen { registration, value ->
			count++
			eventValue = value
			if (count == 2) registration.cancel()
		}
		event.fire("testOne")
		event.fire("testTwo")
		event.fire("testThree")
		assertEquals(2, count)
		assertEquals("testTwo", eventValue)
	}

	@Test
	fun twoListenersCancelBothInSecond() {
		val eventOneRegistration = event.listen { _, value ->
			count++
			eventValue = value
		}
		event.listen { registration, value ->
			count++
			eventValue = value
			registration.cancel()
			eventOneRegistration.cancel()
		}
		event.fire("testOne")
		assertEquals(2, count)
		assertEquals("testOne", eventValue)

		event.fire("testTwo")
		assertEquals(2, count)
		assertEquals("testOne", eventValue)
	}

	@Test
	fun twoListenersAddSecondWhileRunning() {
		event.listen { eventOneRegistration, value1 ->
			count++
			eventValue = value1
			if (count == 1)
				event.listenOnce { value2 ->
					count++
					eventValue = value2
					eventOneRegistration.cancel()
				}
		}
		event.fire("testOne")
		assertEquals(1, count)
		assertEquals("testOne", eventValue)

		event.fire("testTwo")
		assertEquals(3, count)
		assertEquals("testTwo", eventValue)

		event.fire("testThree")
		assertEquals(3, count)
		assertEquals("testTwo", eventValue)
	}
}


