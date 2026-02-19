package renetik.android.event.property

import renetik.android.core.lang.variable.setFalse
import renetik.android.core.lang.variable.setTrue
import renetik.android.event.property.CSProperty.Companion.property

typealias CSActionInterface = CSProperty<Boolean>

operator fun CSActionInterface.invoke() = setTrue()
fun CSActionInterface.start() = setTrue()
fun CSActionInterface.stop() = setFalse()

object CSAction {
    fun action(): CSActionInterface = property(false)
}