package com.omarsalinas.btmessenger.common

import android.bluetooth.BluetoothAdapter
import android.content.Intent

class BtHelper {

    companion object {
        const val REQUEST_ENABLE_BLUETOOTH: Int = 0x0002

        fun getEnableIntent(): Intent {
            return Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        }
    }

    private val adapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    val enabled: Boolean get() = adapter.isEnabled
    val address: String get() = adapter.address
    val scanning: Boolean get() = adapter.isDiscovering

    fun cancelScan() {
        adapter.cancelDiscovery()
    }

    fun startScan() {
        adapter.startDiscovery()
    }
}