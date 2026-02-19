package renetik.android.event.property

import renetik.android.core.lang.variable.setFalse
import renetik.android.core.lang.variable.setTrue

typealias CSActionInterface = CSProperty<Boolean>

fun CSActionInterface.start() = setTrue()
fun CSActionInterface.stop() = setFalse()
