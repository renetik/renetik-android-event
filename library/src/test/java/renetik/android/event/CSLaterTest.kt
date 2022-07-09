package renetik.android.event

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLooper.runUiThreadTasks
import renetik.android.event.util.CSLater.later

/**
 * Post function on main thread with registration support
 */
@RunWith(RobolectricTestRunner::class)
class CSLaterTest {
    @Test
    fun testUnregisteredAfterNilled() {
        var count = 0
        later { count++ }
        runUiThreadTasks()
        assertEquals(1, count)

        val registration = later {
            count++
        }
        registration.cancel()
        runUiThreadTasks()
        assertEquals(1, count)
    }
}
