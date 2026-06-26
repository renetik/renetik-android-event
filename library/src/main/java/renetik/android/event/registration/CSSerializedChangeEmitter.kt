package renetik.android.event.registration

import renetik.android.core.lang.synchronized

internal class CSSerializedChangeEmitter<Change>(
    private val registration: CSRegistration,
    private val onChange: (Change) -> Unit,
) {
    private class Emission<Change>(val value: Change)

    private val lock = Any()
    private val changes = ArrayDeque<Change>()
    private var isDraining = false

    fun update(function: () -> Unit) = synchronized<Any, Unit>(lock) {
        function()
    }

    fun enqueue(produce: () -> Change) {
        var shouldDrain = false
        synchronized<Any, Unit>(lock) {
            changes.addLast(produce())
            if (!isDraining) {
                isDraining = true
                shouldDrain = true
            }
        }
        if (shouldDrain) drain()
    }

    private fun drain() {
        var completed = false
        try {
            while (true) {
                val emission = synchronized<Any, Emission<Change>?>(lock) {
                    if (!registration.isActive) {
                        changes.clear()
                        isDraining = false
                        null
                    } else if (changes.isEmpty()) {
                        isDraining = false
                        null
                    } else Emission(changes.removeFirst())
                } ?: run {
                    completed = true
                    return
                }
                if (registration.isActive) onChange(emission.value)
            }
        } finally {
            if (!completed) synchronized<Any, Unit>(lock) {
                isDraining = false
            }
        }
    }
}
