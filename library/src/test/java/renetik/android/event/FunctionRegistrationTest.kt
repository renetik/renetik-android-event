package renetik.android.event

import org.junit.Assert
import org.junit.Test
import renetik.android.event.registration.CSFunctionRegistration
import renetik.android.event.registration.paused

class FunctionRegistrationTest {
    @Test
    fun testCancelWhilePaused() {
        var count = 0
        val registration = CSFunctionRegistration { count += 1 }
        registration.paused {
            registration.invoke()
            registration.cancel()
        }
        registration.invoke()
        Assert.assertEquals(0, count)
        Assert.assertFalse(registration.isActive)
    }
}