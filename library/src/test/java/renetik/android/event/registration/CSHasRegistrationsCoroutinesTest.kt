package renetik.android.event.registration

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import renetik.android.core.lang.variable.setFalse
import renetik.android.event.common.CSModel
import renetik.android.event.common.destruct
import renetik.android.event.property.CSProperty.Companion.property
import renetik.android.testing.CSAssert.assert

@OptIn(ExperimentalCoroutinesApi::class)
class CSHasRegistrationsCoroutinesTest {

    @Before
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun launch() = runTest {
        val model = CSModel()
        var count = 0
        fun launch() = model.launch { count++ }
        launch()
        launch()
        advanceUntilIdle()
        assert(2, count)
        launch()
        model.destruct()
        advanceUntilIdle()
        assert(2, count)
    }

    @Test
    fun launchIfNot() = runTest {
        val model = CSModel()
        var value = 2
        model.launchIfNot("launchIfNot") { value = 2 }
        model.launchIfNot("launchIfNot") { value = 4 }
        advanceUntilIdle()
        assert(2, value)
    }

    @Test
    fun launchReplace() = runTest {
        val model = CSModel()
        var count = 0
        var value = 2
        model.launch("replace") {
            count += 1
            value = 2
        }
        model.launch("replace") {
            count += 1
            value = 4
        }
        advanceUntilIdle()
        assert(1, count)
        assert(4, value)
    }

    @Test
    fun launchFromLaunch() = runTest {
        val model = CSModel()
        var count = 0
        var value = 2
        model.launch {
            count += 1
            value = 2
            Dispatchers.Main.launch {
                count += 1
                value = 4
            }
        }
        advanceUntilIdle()
        assert(2, count)
        assert(4, value)
    }


    @Test
    fun launchFromLaunchCancel() = runTest {
        val wait1 = property(true)
        val wait2 = property(true)
        val model = CSModel()
        var count = 0
        model.launch {
            count += 1
            wait1.waitIsFalse()
            Job()
//           val ss =  (it.job + Main)
            Main.context {
                count += 1
                wait2.waitIsFalse()
                Main.launch {
                    count += 1
                }
            }
        }
        advanceTimeBy(30)
        assert(1, count)
        wait1.setFalse()
        advanceUntilIdle()
        assert(2, count)
        model.destruct()
        wait2.setFalse()
        advanceUntilIdle()
        assert(2, count)
    }

}