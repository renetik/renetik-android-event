package renetik.android.event.registration

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import renetik.android.core.lang.result.mainScope
import renetik.android.event.common.CSModel
import renetik.android.event.common.destruct
import renetik.android.testing.CSAssert.assert
import renetik.android.testing.TestApplication
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class CSHasRegistrationsLaunchRepeatTest {

    @Before
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun launchRepeatStopped() = runTest {
        mainScope = this
        val model = CSModel()
        var count = 0
        val registration = model.launchRepeat(delay = 5, start = false) { count++ }
        advanceTimeBy(10.milliseconds)
        assert(0, count)
        registration.resume()
        advanceTimeBy(6.milliseconds)
        assert(1, count)
        registration.pause()
        advanceTimeBy(10.milliseconds)
        assert(1, count)
        registration.resume()
        advanceTimeBy(6.milliseconds)
        assert(2, count)
        model.destruct()
        advanceTimeBy(10.milliseconds)
        assert(2, count)
    }

    @Test
    fun launchRepeatStarted() = runTest {
        mainScope = this
        val model = CSModel()
        var count = 0
        val registration = model.launchRepeat(delay = 5, start = true) { count++ }
        advanceTimeBy(6.milliseconds)
        assert(1, count)
        registration.pause()
        advanceTimeBy(10.milliseconds)
        assert(1, count)
        registration.resume()
        advanceTimeBy(6.milliseconds)
        assert(2, count)
        model.destruct()
        advanceTimeBy(10.milliseconds)
        assert(2, count)
    }
}