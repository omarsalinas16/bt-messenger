package com.omarsalinas.btmessenger.controllers

import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import com.omarsalinas.btmessenger.R
import com.omarsalinas.btmessenger.common.*
import com.omarsalinas.btmessenger.controllers.adapters.DevicesAdapter
import com.omarsalinas.btmessenger.models.Device
import kotlinx.android.synthetic.main.fragment_devices.*
import kotlinx.android.synthetic.main.fragment_devices.view.*

class DevicesFragment : SimpleFragment(), DevicesAdapter.Callbacks {

    companion object {
        fun newInstance(): DevicesFragment {
            return DevicesFragment()
        }
    }

    private var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val bluetoothReceiver: BluetoothReceiver = BluetoothReceiver()
    private var callbacks: Callbacks? = null

    private var devices: ArrayList<Device> = arrayListOf()
    private var devicesAdapter: DevicesAdapter = DevicesAdapter(this) { this.devices }

    private var scanning: Boolean = false
    private var receiverRegistered: Boolean = false

    private lateinit var recyclerView: RecyclerView
    private lateinit var scanButton: AppCompatButton
    private lateinit var messageText: AppCompatTextView
    private lateinit var spinner: ProgressBar

    override fun getLayoutId(): Int = R.layout.fragment_devices

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        this.callbacks = context as Callbacks
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewsById(view)
        setViewListeners()
    }

    private fun setViewsById(view: View) {
        this.recyclerView = view.fragment_devices_rv
        this.spinner = view.fragment_devices_spinner
        this.scanButton = view.fragment_devices_scan_btn
        this.messageText = view.fragment_devices_message_txt
    }

    private fun setViewListeners() {
        this.recyclerView.layoutManager = LinearLayoutManager(this.activity)
        this.recyclerView.adapter = this.devicesAdapter

        this.scanButton.setOnClickListener { onScanButtonClicked() }
    }

    override fun onStart() {
        super.onStart()

        if (this.bluetoothAdapter != null && this.bluetoothAdapter?.isEnabled == false) {
            val intent = BtController.getEnableIntent()
            startActivityForResult(intent, BtController.REQUEST_ENABLE_BLUETOOTH)
        }

        loadBondedDevices()

        if (this.devicesAdapter.itemCount <= 0) {
            this.messageText.text = getString(R.string.fragment_devices_message_txt_scan)
            setMessageVisibility(true)
        } else {
            setMessageVisibility(false)
        }

        setScanning(false, false)
    }

    override fun onStop() {
        super.onStop()
        setScanning(false, false)
    }

    override fun onDetach() {
        super.onDetach()
        this.callbacks = null
    }

    override fun onDeviceSelected(device: Device) {
        setScanning(false, false)
        this.callbacks?.onDeviceSelected(device)
    }

    private fun onScanButtonClicked() {
        setScanning(!this.scanning)
    }

    private fun loadBondedDevices() {
        this.bluetoothAdapter?.bondedDevices?.forEach {
            it?.let { addDevice(it.name, it.address, true) }
        }
    }

    private fun setScanning(scanning: Boolean, updateText: Boolean = true) {
        this.scanning = scanning

        if (this.scanning) {
            this.devicesAdapter.clear()

            loadBondedDevices()

            if (!this.receiverRegistered) {
                this.activity?.registerReceiver(this.bluetoothReceiver, getReceiverIntentFilter())
                this.receiverRegistered = true
            }

            this.bluetoothAdapter?.startDiscovery()
            setMessageVisibility(false)
        } else {
            if (this.receiverRegistered) {
                this.activity?.unregisterReceiver(this.bluetoothReceiver)
                this.receiverRegistered = false
            }

            val adapter = this.bluetoothAdapter
            if (adapter != null && adapter.isDiscovering) adapter.cancelDiscovery()

            if (updateText && this.devicesAdapter.itemCount <= 0) {
                this.messageText.text = getString(R.string.fragment_devices_message_txt_no_devices)
                setMessageVisibility(true)
            }
        }

        setSpinnerVisible(this.scanning)
        setScanButtonState(this.scanning)
    }

    private fun setScanButtonState(scanning: Boolean) {
        AppUtils.startTransition(this.fragment_devices_container)

        this.scanButton.text = if (scanning) {
            this.activity?.getString(R.string.fragment_devices_scan_btn_stop)
        } else {
            this.activity?.getString(R.string.fragment_devices_scan_btn_start)
        }

        this.scanButton.setCompoundDrawablesWithIntrinsicBounds(
                if (scanning) R.drawable.ic_bluetooth_searching_white
                else R.drawable.ic_bluetooth_white,
                0, 0, 0
        )
    }

    private fun addDevice(name: String?, address: String, paired: Boolean = false) {
        this.devicesAdapter.add(Device(
                if (AppUtils.stringNotEmpty(name)) name else getString(R.string.unknown),
                address, paired)
        )
    }

    private fun setSpinnerVisible(visible: Boolean) {
        AppUtils.startTransition(this.fragment_devices_container)
        this.spinner.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun setMessageVisibility(visible: Boolean) {
        AppUtils.startTransition(this.fragment_devices_main)
        AppUtils.setVisibility(this.messageText, visible)
    }

    private fun getReceiverIntentFilter(): IntentFilter {
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)

        return filter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            BtController.REQUEST_ENABLE_BLUETOOTH -> {
                if (resultCode != RESULT_OK) {
                    val activity = this.activity
                    if (activity != null) AppUtils.getNoBluetoothErrorDialog(activity).show(this.fragmentManager)
                }
            }
        }
    }

    internal interface Callbacks {
        fun onDeviceSelected(palDevice: Device)
    }

    private inner class BluetoothReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    device?.let { addDevice(it.name, it.address) }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    setScanning(false)
                }
            }
        }

    }

}