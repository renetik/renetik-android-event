@file:OptIn(ExperimentalCoroutinesApi::class)

package renetik.android.event

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import renetik.android.event.common.CSLaterOnceFunc.Companion.funLaterOnce
import renetik.android.event.common.CSModel
import renetik.android.event.common.destruct
import renetik.android.testing.CSAssert.assert

@RunWith(RobolectricTestRunner::class)
class CSLaterOnceTest {

    @Before
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun laterOnceTest() = runTest {
        var count = 0
        val model = CSModel()
        val laterOnceFunc = model.funLaterOnce { count++ }

        laterOnceFunc()
        laterOnceFunc()
        advanceUntilIdle()
        assert(expected = 1, count)

        laterOnceFunc()
        advanceUntilIdle()
        laterOnceFunc()
        model.destruct()
        advanceUntilIdle()
        assert(expected = 2, count)
    }
}
