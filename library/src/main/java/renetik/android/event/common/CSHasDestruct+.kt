package renetik.android.event.common

import renetik.android.core.lang.Func
import renetik.android.event.delegate
import renetik.android.event.listen
import renetik.android.event.registration.CSHasChangeValue
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.invoke
import renetik.android.event.registration.plus

fun <T : CSHasDestruct> T.destruct() = onDestruct()

inline fun CSHasDestruct.onDestructed(crossinline func: Func){
    eventDestruct.listen(func)
}

inline fun CSHasDestruct.onDestructed(
    parent: CSHasRegistrations, crossinline func: Func
) {
    var registration: CSRegistration? = null
    registration = parent + eventDestruct.invoke {
        registration?.cancel()
        func()
    }
}

val CSHasDestruct.isDestruct: CSHasChangeValue<Boolean>
    get() = eventDestruct.delegate { isDestructed }