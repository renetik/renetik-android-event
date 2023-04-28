package renetik.android.event.common

import android.content.BroadcastReceiver
import android.content.Intent.ACTION_HEADSET_PLUG
import renetik.android.core.extensions.content.register
import renetik.android.core.logging.CSLog.logDebug
import renetik.android.core.logging.CSLog.logWarn
import renetik.android.core.logging.CSLogMessage.Companion.message

class CSHeadsetAudioPlugDetector(
    parent: CSContext,
    val onHeadsetPlugChanged: (isPlugged: Boolean) -> Unit) : CSContext(parent) {

    private var isPlugged: Boolean? = null

    private val receiver = register(ACTION_HEADSET_PLUG) { intent, receiver ->
        if (intent.action == ACTION_HEADSET_PLUG)
            when (val state = intent.getIntExtra("state", -1)) {
                0, 1 -> onStateReceived(state, receiver)
                else -> logWarn { "ACTION_HEADSET_PLUG unknown " }
            }
    }

    private fun onStateReceived(state: Int, receiver: BroadcastReceiver) {
        val isPlugged = state != 0
        if (this.isPlugged != isPlugged) {
            this.isPlugged = isPlugged
            logDebug { "ACTION_HEADSET_PLUG isUnplugged:$isPlugged" }
            if (!receiver.isInitialStickyBroadcast) onHeadsetPlugChanged(isPlugged)
        }
    }

    override fun onDestruct() {
        super.onDestruct()
        unregisterReceiver(receiver)
    }
}