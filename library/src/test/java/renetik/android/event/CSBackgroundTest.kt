package renetik.android.event

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import renetik.android.core.lang.atomic.CSAtomic.Companion.atomic
import renetik.android.testing.CSAssert.assert
import kotlin.time.Duration.Companion.seconds

class CSBackgroundTest {

    @Test
    fun background() {
        var int by atomic(0)
        CSBackground.background { int += 1 }
        assert(expected = 0, actual = int)
        runBlocking { delay(1.seconds) }
        assert(expected = 1, actual = int)
    }

    @Test
    fun backgroundCancel() {
        var int by atomic(0)
        val registration = CSBackground.background { int += 1 }
        assert(expected = 0, actual = int)
        registration.cancel()
        runBlocking { delay(1.seconds) }
        assert(expected = 0, actual = int)
    }

    @Test
    fun backgroundCanceled() {
        var int by atomic(0)
        val registration = CSBackground.background { int += 1; it.cancel() }
        assert(expected = 0, actual = int)
        runBlocking { delay(1.seconds) }
        assert(expected = 1, actual = int)
        assert(registration.isCanceled)
    }
}