package com.omarsalinas.btmessenger.common

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.provider.Settings
import java.util.*
import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class BtHelper {

    companion object {
        private const val APP_NAME: String = "BtMessenger"
        private val SERVER_UUID: UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66")

        const val REQUEST_ENABLE_BLUETOOTH: Int = 0x0002
        const val MESSAGE_STATE_CHANGED: Int = 0x0003
        const val MESSAGE_READ: Int = 0x0004
        const val MESSAGE_WRITE: Int = 0x0005
        const val MESSAGE_DEVICE_OBJECT: Int = 0x0006
        const val MESSAGE_TOAST: Int = 0x0007
        const val DEVICE_OBJECT = "device_name"

        private const val SECURE_BLUETOOTH_ADDRESS: String = "bluetooth_address"

        fun getEnableIntent(): Intent {
            return Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        }

        fun getAddress(context: Context): String {
            return Settings.Secure.getString(context.contentResolver, SECURE_BLUETOOTH_ADDRESS)
        }
    }

    /*
    private inner class ReadWriteThread(private val bluetoothSocket: BluetoothSocket) : Thread() {
        private val inputStream: InputStream?
        private val outputStream: OutputStream?

        init {
            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null

            try {
                tmpIn = bluetoothSocket.inputStream
                tmpOut = bluetoothSocket.outputStream
            } catch (e: IOException) { }

            this.inputStream = tmpIn
            this.outputStream = tmpOut
        }

        override fun run() {
            val buffer = ByteArray(1024)
            var bytes: Int

            while (true) {
                try {
                    bytes = this.inputStream!!.read(buffer)
                    handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget()
                } catch (e: IOException) {
                    connectionLost()
                    // Start the service over to restart listening mode
                    this@ChatController.start()
                    break
                }
            }
        }

        fun write(buffer: ByteArray) {
            try {
                this.outputStream!!.write(buffer)
                handler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer).sendToTarget()
            } catch (e: IOException) { }
        }

        fun cancel() {
            try { this.bluetoothSocket.close() }
            catch (e: IOException) { e.printStackTrace() }
        }

    }
    */
}
