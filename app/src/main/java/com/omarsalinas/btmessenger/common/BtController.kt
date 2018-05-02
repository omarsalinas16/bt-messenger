package com.omarsalinas.btmessenger.common

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.provider.Settings
import java.util.*
import android.bluetooth.BluetoothSocket
import android.support.annotation.NonNull
import org.jetbrains.annotations.NotNull
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.bluetooth.BluetoothServerSocket

class BtController(
        @NonNull @NotNull private val handler: BtHandler
) {

    companion object {
        private const val APP_NAME: String = "BtMessenger"
        private val SERVER_UUID: UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66")

        const val REQUEST_ENABLE_BLUETOOTH: Int = 0x0002
        const val MESSAGE_STATE_CHANGED: Int = 0x0003
        const val MESSAGE_READ: Int = 0x0004
        const val MESSAGE_WRITE: Int = 0x0005
        const val MESSAGE_DEVICE_OBJECT: Int = 0x0006
        const val BUNDLE_DEVICE: String = "com.omarsalinas.btmessenger.device"

        private const val SECURE_BLUETOOTH_ADDRESS: String = "bluetooth_address"

        fun getEnableIntent(): Intent {
            return Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        }

        fun getAddress(context: Context): String {
            return Settings.Secure.getString(context.contentResolver, SECURE_BLUETOOTH_ADDRESS)
        }
    }

    private var adapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private var acceptThread: AcceptThread? = null
    private var connectThread: ConnectThread? = null
    private var connectedThread: ReadWriteThread? = null

    private var state: BtHelperState = BtHelperState.IDLE

    @Synchronized
    private fun setState(state: BtHelperState) {
        this.state = state
        handler.obtainMessage(MESSAGE_STATE_CHANGED, state.value, -1).sendToTarget()
    }

    @Synchronized
    fun getState() = this.state

    private fun cancelConnectThread() {
        this.connectThread?.cancel()
        this.connectThread = null
    }

    private fun cancelConnectedThread() {
        this.connectedThread?.cancel()
        this.connectedThread = null
    }

    private fun cancelAcceptedThread() {
        this.acceptThread?.cancel()
        this.acceptThread = null
    }

    private fun cancelAllThreads() {
        cancelConnectThread()
        cancelConnectedThread()
        cancelAcceptedThread()
    }

    @Synchronized
    fun start() {
        cancelConnectThread()
        cancelConnectedThread()

        if (this.acceptThread == null) {
            this.acceptThread = AcceptThread()
            this.acceptThread?.start()
        }

        setState(BtHelperState.LISTEN)
    }

    @Synchronized
    fun stop() {
        cancelAllThreads()
        setState(BtHelperState.IDLE)
    }

    // initiate connection to remote device
    @Synchronized
    fun connect(device: BluetoothDevice) {
        if (this.state == BtHelperState.CONNECTING) {
            cancelConnectThread()
        }

        cancelConnectedThread()

        // Start the thread to connect with the given device
        this.connectThread = ConnectThread(device)
        this.connectThread?.start()

        setState(BtHelperState.CONNECTING)

    }

    // manage Bluetooth connection
    @Synchronized
    fun connected(socket: BluetoothSocket, device: BluetoothDevice) {
        cancelAllThreads()

        // Start the thread to manage the connection and perform transmissions
        this.connectedThread = ReadWriteThread(socket)
        this.connectedThread?.start()

        // Send the name of the connected device back to the UI Activity
        val msg = handler.obtainMessage(MESSAGE_DEVICE_OBJECT)

        val bundle = Bundle()
        bundle.putParcelable(BUNDLE_DEVICE, device)

        msg.data = bundle

        this.handler.sendMessage(msg)

        setState(BtHelperState.CONNECTED)
    }

    fun write(out: ByteArray) {
        var r: ReadWriteThread? = null

        synchronized(this) {
            if (this.state !== BtHelperState.CONNECTED) return
            r = this.connectedThread
        }

        r?.write(out)
    }

    private fun connectionFailed() {
        setState(BtHelperState.NO_CONNECTION)
        this.start()
    }

    private fun connectionLost() {
        setState(BtHelperState.CONNECTION_LOST)
        this.start()
    }

    private inner class AcceptThread : Thread() {

        private var serverSocket: BluetoothServerSocket? = null

        init {
            try {
                this.serverSocket = this@BtController.adapter?.listenUsingInsecureRfcommWithServiceRecord(APP_NAME, SERVER_UUID)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }

        override fun run() {
            this.name = "AcceptThread"

            var socket: BluetoothSocket?

            while (this@BtController.state !== BtHelperState.CONNECTED) {
                try {
                    socket = serverSocket!!.accept()
                } catch (e: IOException) {
                    break
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized(this@BtController) {
                        when (this@BtController.state) {
                            BtHelperState.LISTEN, BtHelperState.CONNECTING ->
                                // start the connected thread.
                                this@BtController.connected(socket, socket.remoteDevice)
                            BtHelperState.IDLE, BtHelperState.CONNECTED ->
                                // Either not ready or already connected. Terminate
                                // new socket.
                                try {
                                    socket.close()
                                } catch (e: IOException) {
                                }
                            else -> { }
                        }
                    }
                }
            }
        }

        fun cancel() {
            try {
                serverSocket?.close()
            } catch (e: IOException) {
            }
        }

    }

    private inner class ConnectThread(private val device: BluetoothDevice) : Thread() {

        private var socket: BluetoothSocket? = null

        init {
            try {
                this.socket = device.createInsecureRfcommSocketToServiceRecord(SERVER_UUID)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        override fun run() {
            this.name = "ConnectThread"

            // Always cancel discovery because it will slow down a connection
            this@BtController.adapter?.cancelDiscovery()

            // Make a connection to the BluetoothSocket
            try {
                socket!!.connect()

                // Reset the ConnectThread because we're done
                synchronized(this@BtController) { connectThread = null }

                // Start the connected thread
                this@BtController.connected(socket!!, device)
            } catch (e: IOException) {
                try {
                    socket!!.close()
                } catch (e2: IOException) {
                }

                this@BtController.connectionFailed()
                return
            }
        }

        fun cancel() {
            try {
                socket!!.close()
            } catch (e: IOException) {
            }
        }

    }

    private inner class ReadWriteThread(private val bluetoothSocket: BluetoothSocket) : Thread() {

        private var inputStream: InputStream? = null
        private var outputStream: OutputStream? = null

        init {
            try {
                this.inputStream = bluetoothSocket.inputStream
                this.outputStream = bluetoothSocket.outputStream
            } catch (e: IOException) {
            }
        }

        override fun run() {
            val buffer = ByteArray(1024)
            var bytes: Int

            while (true) {
                try {
                    bytes = this.inputStream!!.read(buffer)
                    this@BtController.handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget()
                } catch (e: IOException) {
                    this@BtController.connectionLost()
                    // Start the service over to restart listening mode
                    this@BtController.start()
                    break
                }
            }
        }

        fun write(buffer: ByteArray) {
            try {
                this.outputStream!!.write(buffer)
                this@BtController.handler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer).sendToTarget()
            } catch (e: IOException) {
            }
        }

        fun cancel() {
            try {
                this.bluetoothSocket.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }

}
