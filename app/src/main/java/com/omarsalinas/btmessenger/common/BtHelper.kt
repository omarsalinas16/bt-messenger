package com.omarsalinas.btmessenger.common

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent

class BtHelper {

    companion object {
        const val REQUEST_ENABLE_BLUETOOTH: Int = 0x0002

        fun getEnableIntent(): Intent {
            return Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        }
    }

    private val adapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    val enabled: Boolean get() = adapter?.isEnabled ?: false
    val scanning: Boolean get() = adapter?.isDiscovering ?: false

    val address: String @SuppressLint("HardwareIds")
        get() = adapter?.address ?: "00:00:00:00:00:00"

    fun cancelScan() {
        if (this.scanning) adapter?.cancelDiscovery()
    }

    fun startScan() {
        adapter?.startDiscovery()
    }
}