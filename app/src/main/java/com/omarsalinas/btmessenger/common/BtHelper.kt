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

    val enabled: Boolean
        get() = adapter?.isEnabled ?: throw Exception("Could not obtain BluetoothAdapter isEnabled")

    val scanning: Boolean
        get() = adapter?.isDiscovering ?: throw Exception("Could not obtain BluetoothAdapter isDiscovering")

    val address: String @SuppressLint("HardwareIds")
        get() = adapter?.address ?: throw Exception("Could not obtain BluetoothAdapter address")

    fun cancelScan() {
        if (this.scanning) adapter?.cancelDiscovery()
    }

    fun startScan() {
        adapter?.startDiscovery()
    }
}