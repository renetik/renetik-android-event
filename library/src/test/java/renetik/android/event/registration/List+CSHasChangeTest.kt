package renetik.android.event.registration

import org.junit.Test
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.invoke
import renetik.android.testing.CSAssert.assert

class ListCSHasChangeTest {
    @Test
    fun delegateChild() {
        val event1 = event()
        val event2 = event()
        val list: List<CSHasChange<Unit>> = listOf(event1, event2)
        var onChangeFired = 0
        val registration = list.onChange {
            onChangeFired++
        }
        event1()
        assert(expected = 1, actual = onChangeFired)
        event2()
        assert(expected = 2, actual = onChangeFired)
        registration.paused { event1() }
        assert(expected = 2, actual = onChangeFired)
        event1()
        assert(expected = 3, actual = onChangeFired)
    }
}