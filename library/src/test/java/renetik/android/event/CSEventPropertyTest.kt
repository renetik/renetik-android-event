package renetik.android.event

import org.junit.Assert.assertEquals
import org.junit.Test
import renetik.android.event.property.CSEventProperty
import renetik.android.event.property.CSEventPropertyFunctions.property
import renetik.android.event.property.onChange

class CSEventPropertyTest {

	private var eventCounter = 0
	private val property: CSEventProperty<String> = property("")

	@Test
	fun fireTwiceAndCancel() {
		property.onChange { registration, _ ->
			eventCounter++
			if (eventCounter == 2) registration.cancel()
		}
		property.value = "testOne"
		property.value = "testTwo"
		property.value = "testThree"
		assertEquals(2, eventCounter)
		assertEquals("testThree", property.value)
	}

	@Test
	fun twoListenersCancelBothInSecond() {
		val eventOneRegistration = property.onChange {
			eventCounter++
		}
		property.onChange { registration, _ ->
			eventCounter++
			registration.cancel()
			eventOneRegistration.cancel()
		}
		property.value = "testOne"
		assertEquals(2, eventCounter)
		assertEquals("testOne", property.value)

		property.value = "testTwo"
		assertEquals(2, eventCounter)
		assertEquals("testTwo", property.value)
	}

	@Test
	fun twoListenersAddSecondWhileRunning() {
		property.onChange { eventOneRegistration, _ ->
			eventCounter++
			if (eventCounter == 1)
				property.onChange { registration, _ ->
					eventCounter++
					registration.cancel()
					eventOneRegistration.cancel()
				}
		}

		property.value = "testOne"
		assertEquals(1, eventCounter)

		property.value = "testTwo"
		assertEquals(3, eventCounter)

		property.value = "testThree"
		assertEquals(3, eventCounter)
	}
}


