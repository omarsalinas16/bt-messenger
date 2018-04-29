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
        get() = adapter?.isEnabled ?: false

    val scanning: Boolean
        get() = adapter?.isDiscovering ?: false

    val address: String @SuppressLint("HardwareIds")
        get() = adapter?.address ?: throw BtHelperException("Could not obtain BluetoothAdapter address")

    val name: String
        get() = adapter?.name ?: throw BtHelperException("Could not obtain BluetoothAdapter name")

    fun cancelScan() {
        if (this.scanning) adapter?.cancelDiscovery()
    }

    fun startScan() {
        adapter?.startDiscovery()
    }
}

class BtHelperException(message: String) : Exception(message)