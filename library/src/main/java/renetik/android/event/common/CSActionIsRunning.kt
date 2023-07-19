package renetik.android.event.common

import renetik.android.core.kotlin.collections.list
import renetik.android.event.property.CSActionInterface
import renetik.android.event.property.CSProperty
import renetik.android.event.property.CSProperty.Companion.property
import renetik.android.event.property.CSPropertyWrapper
import renetik.android.event.registration.CSHasChangeValue.Companion.action
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import renetik.android.event.registration.cancel
import renetik.android.event.registration.register

class CSActionIsRunning(
    parent: CSHasDestruct,
    vararg actions: CSActionInterface
) : CSPropertyWrapper<Boolean>(parent) {
    override val property = property(false)
    private val runningActions = list<CSProperty<Boolean>>()

    init {
        add(actions)
    }

    private fun add(actions: Array<out CSActionInterface>) = actions.forEach { add(it) }

    fun add(action: CSActionInterface): CSRegistration {
        fun updateIsRunning(isRunning: Boolean) {
            if (isRunning) runningActions.add(action) else runningActions.remove(action)
            property.value(runningActions.size > 0)
        }

        val actionOnChange = register(action.action(::updateIsRunning))

        return CSRegistration(onCancel = {
            cancel(actionOnChange)
            runningActions.remove(action)
        })
    }
}