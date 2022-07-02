package renetik.android.event.property

import renetik.android.core.lang.property.isTrue
import renetik.android.core.lang.property.setFalse
import renetik.android.core.lang.property.setTrue
import renetik.android.event.property.CSEventPropertyFunctions.property

typealias CSActionInterface = CSEventProperty<Boolean>

fun CSActionInterface.start() = setTrue()
fun CSActionInterface.stop() = setFalse()
val CSActionInterface.isRunning get() = isTrue

object CSAction {
    fun action(): CSActionInterface = property(false)
}