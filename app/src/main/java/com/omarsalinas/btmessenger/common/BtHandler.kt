package com.omarsalinas.btmessenger.common

import android.bluetooth.BluetoothDevice
import android.os.Handler
import android.os.Message
import android.support.annotation.NonNull
import org.jetbrains.annotations.NotNull

class BtHandler(
        @NonNull @NotNull private val callbacks: Callbacks
) : Handler() {

    override fun handleMessage(msg: Message?) {
        val what = msg?.what

        when (what) {
            BtController.MESSAGE_STATE_CHANGED -> {
                val state = BtHelperState.fromValue(msg.arg1)
                this.callbacks.onMessageStateChanged(state)
            }
            BtController.MESSAGE_READ -> {
                val message = String(msg.obj as ByteArray, 0, msg.arg1)
                this.callbacks.onMessageRead(message)
            }
            BtController.MESSAGE_DEVICE_OBJECT -> {
                val device = msg.data.getParcelable<BluetoothDevice>(BtController.BUNDLE_DEVICE)
                this.callbacks.onMessageDeviceObject(device)
            }
        }
    }

    interface Callbacks {
        fun onMessageStateChanged(state: BtHelperState)
        fun onMessageRead(message: String)
        fun onMessageDeviceObject(device: BluetoothDevice)
    }

}