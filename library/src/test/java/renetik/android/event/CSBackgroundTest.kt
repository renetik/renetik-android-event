package renetik.android.event

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import renetik.android.core.lang.atomic.CSAtomic.Companion.atomic
import renetik.android.event.CSBackground.background
import renetik.android.testing.CSAssert.assert
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class CSBackgroundTest {

    @Test
    fun background() {
        var int by atomic(0)
        background(after = 10.milliseconds) { int += 1 }
//      wrong assumption, it can be 1 as well...
//      assert(expected = 0, actual = int)
        runBlocking { delay(1.seconds) }
        assert(expected = 1, actual = int)
    }

    @Test
    fun backgroundCancel() {
        var int by atomic(0)
        val registration = background(after = 500.milliseconds) { int += 1 }
        assert(expected = 0, actual = int)
        registration.cancel()
        runBlocking { delay(1.seconds) }
        assert(expected = 0, actual = int)
    }

    @Test
    fun backgroundCanceled() {
        var int by atomic(0)
        val registration = background(after = 500.milliseconds) { int += 1; it.cancel() }
        assert(expected = 0, actual = int)
        runBlocking { delay(2.seconds) }
        assert(expected = 1, actual = int)
        assert(registration.isCanceled)
    }
}