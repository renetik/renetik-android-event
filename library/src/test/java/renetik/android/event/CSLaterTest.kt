package renetik.android.event

import renetik.android.event.change.*
import renetik.android.event.dispatch.*
import renetik.android.event.lifecycle.*

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks
import renetik.android.core.lang.CSHandler.mainHandler
import renetik.android.core.lang.send
import renetik.android.event.dispatch.later

@RunWith(RobolectricTestRunner::class)
class CSLaterTest {
    @Test
    fun testUnregisteredAfterNilled() {
        var count = 0
        mainHandler.send { count++ }
        runUiThreadTasksIncludingDelayedTasks()
        assertEquals(1, count)

        val registration = mainHandler.later {
            count++
        }
        registration.cancel()
        runUiThreadTasksIncludingDelayedTasks()
        assertEquals(1, count)
    }
}
