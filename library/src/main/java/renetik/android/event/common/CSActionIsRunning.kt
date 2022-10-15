package renetik.android.event.common


import renetik.android.core.kotlin.collections.list
import renetik.android.core.lang.variable.isTrue
import renetik.android.event.property.CSActionInterface
import renetik.android.event.property.CSProperty
import renetik.android.event.property.CSProperty.Companion.property
import renetik.android.event.property.CSPropertyWrapper
import renetik.android.event.registration.register

class CSActionIsRunning(
    parent: CSHasDestruct,
    vararg actions: CSActionInterface)
    : CSPropertyWrapper<Boolean>(parent) {
    override val property = property(false)
    private val runningActions = list<CSProperty<Boolean>>()

    init {
        add(actions)
    }

    fun add(actions: Array<out CSActionInterface>) = actions.forEach { add(it) }

    fun add(action: CSActionInterface) {
        fun updateIsRunning(isRunning: Boolean) {
            if (isRunning) runningActions.add(action) else runningActions.remove(action)
            property.value(runningActions.size > 0)
        }
        updateIsRunning(action.isTrue)
        register(action.onChange { isTrue -> updateIsRunning(isTrue) })
    }
}