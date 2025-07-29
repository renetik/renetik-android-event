package renetik.android.event.device

import android.Manifest.permission.BLUETOOTH_ADVERTISE
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.bluetooth.BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE
import android.bluetooth.BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES
import android.bluetooth.BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.EXTRA_DEVICE
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothProfile.A2DP
import android.bluetooth.BluetoothProfile.EXTRA_STATE
import android.bluetooth.BluetoothProfile.STATE_CONNECTED
import android.bluetooth.BluetoothProfile.ServiceListener
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioDeviceInfo.TYPE_WIRED_HEADPHONES
import android.media.AudioDeviceInfo.TYPE_WIRED_HEADSET
import android.media.AudioManager.GET_DEVICES_OUTPUTS
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.S
import renetik.android.core.extensions.content.audioManager
import renetik.android.core.extensions.content.isPermissionsGranted
import renetik.android.core.extensions.content.unregister
import renetik.android.core.lang.CSHandler.threadHandler
import renetik.android.core.lang.variable.assign
import renetik.android.event.common.CSContext
import renetik.android.event.property.CSSafePropertyImpl.Companion.safeProperty
import renetik.android.event.registration.CSHasChangeValue
import renetik.android.event.registration.or

class CSHeadsetDetector(parent: CSContext) : CSContext(parent),
    CSHasChangeValue<Boolean> {
    private val isDeviceHeadset = safeProperty(false)
    private val isBtHeadset = safeProperty(false)
    private val isHeadset = isDeviceHeadset or isBtHeadset
    override val value: Boolean get() = isHeadset.value
    override fun onChange(function: (Boolean) -> Unit) = isHeadset.onChange(function)

    // Bluetooth
    @Suppress("DEPRECATION")
    private val btAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private fun onA2dpDevice(device: BluetoothDevice, isConnected: Boolean) =
        when (device.bluetoothClass.deviceClass) {
            AUDIO_VIDEO_HANDSFREE, AUDIO_VIDEO_HEADPHONES,
            AUDIO_VIDEO_WEARABLE_HEADSET -> isBtHeadset assign isConnected
            else -> Unit // ignore A2DP speakers, car stereos, etc.
        }

    private val btwBroadcastReceiver = object : BroadcastReceiver() {
        @Suppress("DEPRECATION")
        override fun onReceive(ctx: Context, intent: Intent) = onA2dpDevice(
            device = intent.getParcelableExtra(EXTRA_DEVICE)!!,
            isConnected = intent.getIntExtra(EXTRA_STATE, -1) == STATE_CONNECTED
        )
    }

    private var btProfileProxyListener: ServiceListener = object : ServiceListener {
        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) =
            onBtProfileProxyServiceConnected(profile, proxy)

        override fun onServiceDisconnected(profile: Int) = Unit
    }

    private fun onBtProfileProxyServiceConnected(profile: Int, proxy: BluetoothProfile) {
        if (profile == A2DP) proxy.connectedDevices.forEach { onA2dpDevice(it, true) }
        btAdapter?.closeProfileProxy(A2DP, proxy)
    }

    init {
        if ((SDK_INT < S || isPermissionsGranted(BLUETOOTH_CONNECT, BLUETOOTH_ADVERTISE))
            && btAdapter != null) {
            btAdapter.getProfileProxy(context, btProfileProxyListener, A2DP)
            context.registerReceiver(btwBroadcastReceiver,
                IntentFilter(ACTION_CONNECTION_STATE_CHANGED))
        }
    }

    // AudioDevice
    private val deviceTypes = arrayOf(TYPE_WIRED_HEADPHONES, TYPE_WIRED_HEADSET)
    private val deviceCallback = object : AudioDeviceCallback() {
        override fun onAudioDevicesAdded(added: Array<AudioDeviceInfo>) {
            if (added.any { it.isSink && it.type in deviceTypes })
                isDeviceHeadset assign true
        }

        override fun onAudioDevicesRemoved(removed: Array<AudioDeviceInfo>) {
            if (removed.any { it.isSink && it.type in deviceTypes })
                isDeviceHeadset assign false
        }
    }

    init {
        audioManager.getDevices(GET_DEVICES_OUTPUTS)
            .filter { it.isSink && it.type in deviceTypes }
            .let { isDeviceHeadset assign it.isNotEmpty() }
        audioManager.registerAudioDeviceCallback(deviceCallback, threadHandler)
    }

    // Cleanup
    override fun onDestruct() {
        context.unregister(btwBroadcastReceiver)
        audioManager.unregisterAudioDeviceCallback(deviceCallback)
        super.onDestruct()
    }
}