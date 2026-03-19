package renetik.android.event

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import renetik.android.core.base.TestCSApplication
import renetik.android.event.registration.CSFunctionRegistration
import renetik.android.event.registration.paused

@RunWith(RobolectricTestRunner::class)
@Config(application = TestCSApplication::class)
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
