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
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.omarsalinas.btmessenger.R
import com.omarsalinas.btmessenger.common.*
import com.omarsalinas.btmessenger.controllers.adapters.DevicesAdapter
import com.omarsalinas.btmessenger.models.User
import kotlinx.android.synthetic.main.fragment_devices.*
import kotlinx.android.synthetic.main.fragment_devices.view.*

class DevicesFragment : SimpleFragment() {

    companion object {
        private const val TAG: String = "DEVICES_FRAGMENT"

        fun newInstance(): DevicesFragment {
            return DevicesFragment()
        }
    }

    private var devices: ArrayList<User> = arrayListOf()

    private var btAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val receiver: BtReceiver = BtReceiver()
    private var callbacks: Callbacks? = null
    private lateinit var adapter: DevicesAdapter

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

        this.adapter = DevicesAdapter(
                { onDeviceSelected(it) },
                { this.devices }
        )

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
        this.recyclerView.adapter = this.adapter

        this.scanButton.setOnClickListener { onScanButtonClicked() }
    }

    override fun onStart() {
        super.onStart()

        this.btAdapter?.let {
            if (!it.isEnabled) {
                val intent = BtController.getEnableIntent()
                startActivityForResult(intent, BtController.REQUEST_ENABLE_BLUETOOTH)
            }
        }

        this.adapter.clear()
        this.btAdapter?.bondedDevices?.forEach {
            it?.let { this.adapter.add(User(it.name, it.address)) }
        }

        setScanning(false)

        if (this.adapter.itemCount <= 0) {
            this.messageText.text = getString(R.string.fragment_devices_message_txt_scan)
            setMessageVisibility(true)
        } else {
            setMessageVisibility(false)
        }
    }

    override fun onStop() {
        super.onStop()
        setScanning(false)
    }

    override fun onDetach() {
        super.onDetach()
        this.callbacks = null
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

    private fun onDeviceSelected(user: User) {
        setScanning(false)
        this.callbacks?.onDeviceSelected(user)
    }

    private fun onScanButtonClicked() {
        setScanning(!this.scanning)
    }

    private fun setScanning(scanning: Boolean) {
        this.scanning = scanning

        if (this.scanning) {
            this.adapter.clear()

            if (!this.receiverRegistered) {
                this.activity?.registerReceiver(this.receiver, getReceiverIntentFilter())
                this.receiverRegistered = true
            }

            this.btAdapter?.startDiscovery()
            setMessageVisibility(false)
        } else {
            if (this.receiverRegistered) {
                this.activity?.unregisterReceiver(this.receiver)
                this.receiverRegistered = false
            }

            val adapter = this.btAdapter
            if (adapter != null && adapter.isDiscovering) adapter.cancelDiscovery()

            if (this.adapter.itemCount <= 0) {
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

    internal interface Callbacks {
        fun onDeviceSelected(pal: User)
    }

    private inner class BtReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    device?.let { adapter.add(User(it.name, it.address)) }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    setScanning(false)
                }
            }
        }

    }

}