package com.omarsalinas.btmessenger.controllers

import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.omarsalinas.btmessenger.R
import com.omarsalinas.btmessenger.common.AppUtils
import com.omarsalinas.btmessenger.common.BtHelper
import com.omarsalinas.btmessenger.common.SimpleFragment
import com.omarsalinas.btmessenger.controllers.adapters.DevicesAdapter
import com.omarsalinas.btmessenger.models.User
import kotlinx.android.synthetic.main.fragment_devices.*
import kotlinx.android.synthetic.main.fragment_devices.view.*
import org.jetbrains.annotations.NotNull

class DevicesFragment : SimpleFragment() {

    companion object {
        private const val TAG: String = "DEVICES_FRAGMENT"
        private const val BUNDLE_USER = "com.omarsalinas.btmessenger.bundle_user"

        fun newInstance(@NonNull @NotNull user: User): DevicesFragment {
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_USER, user)

            val fragment = DevicesFragment()
            fragment.arguments = bundle

            return fragment
        }
    }

    private val btHelper: BtHelper = BtHelper()
    private val receiver: BtReceiver = BtReceiver()
    private var callbacks: Callbacks? = null
    private lateinit var adapter: DevicesAdapter

    private var scanning: Boolean = false
    private var receiverRegistered: Boolean = false

    private lateinit var recyclerView: RecyclerView
    private lateinit var scanButton: AppCompatButton
    private lateinit var spinner: ProgressBar

    override fun getLayoutId(): Int = R.layout.fragment_devices

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        this.callbacks = context as Callbacks
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = BtHelper.getEnableIntent()
        startActivityForResult(intent, BtHelper.REQUEST_ENABLE_BLUETOOTH)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.adapter = DevicesAdapter(this.activity) {
            onDeviceSelected(it)
        }

        setViewsById(view)
    }

    private fun setViewsById(view: View) {
        this.recyclerView = view.fragment_devices_rv
        this.recyclerView.layoutManager = LinearLayoutManager(this.activity)
        this.recyclerView.adapter = this.adapter

        this.spinner = view.fragment_devices_spinner
        this.spinner.isIndeterminate = true

        this.scanButton = view.fragment_devices_scan_btn
        this.scanButton.setOnClickListener { onScanButtonClicked() }
    }

    override fun onStart() {
        super.onStart()
        setScanning(false)
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
            BtHelper.REQUEST_ENABLE_BLUETOOTH -> {
                if (resultCode != RESULT_OK) {
                    AppUtils.getNoBluetoothErrorDialog(this.activity!!).show(this.fragmentManager)
                }
            }
        }
    }

    private fun onDeviceSelected(device: BluetoothDevice) {
        setScanning(false)
        this.callbacks?.onDeviceSelected(device)
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

            this.btHelper.startScan()
        } else {
            if (this.receiverRegistered) {
                this.activity?.unregisterReceiver(this.receiver)
                this.receiverRegistered = false
            }

            if (this.btHelper.scanning) this.btHelper.cancelScan()
        }

        setSpinnerVisible(this.scanning)
        setScanButtonState(this.scanning)
    }

    private fun setScanButtonState(scanning: Boolean) {
        try {
            TransitionManager.beginDelayedTransition(this.fragment_devices_container)
        } catch (e: Exception) {
            Log.e(TAG, "Ignored transition, error: $e")
        }

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
        try {
            TransitionManager.beginDelayedTransition(this.fragment_devices_container)
        } catch (e: Exception) {
            Log.e(TAG, "Ignored transition, error: $e")
        }

        this.spinner.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun getReceiverIntentFilter(): IntentFilter {
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)

        return filter
    }

    internal interface Callbacks {
        fun onDeviceSelected(device: BluetoothDevice)
    }

    private inner class BtReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    adapter.add(device)
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    setScanning(false)
                }
            }
        }

    }

}