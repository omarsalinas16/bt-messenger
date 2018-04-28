package com.omarsalinas.btmessenger.controllers.adapters

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.omarsalinas.btmessenger.R
import com.omarsalinas.btmessenger.common.AppUtils
import com.omarsalinas.btmessenger.common.SimpleAdapter
import com.omarsalinas.btmessenger.common.SimpleHolder
import kotlinx.android.synthetic.main.item_device.view.*

class DevicesAdapter(
        private val activity: Activity?,
        private val onDeviceSelected: (device: BluetoothDevice) -> Unit
) : SimpleAdapter<BluetoothDevice, DevicesAdapter.DevicesHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevicesHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)
        return DevicesHolder(view)
    }

    inner class DevicesHolder(view: View) : SimpleHolder<BluetoothDevice>(view) {

        private lateinit var device: BluetoothDevice

        private val nameText: TextView = view.item_device_name_txt
        private val addressText: TextView = view.item_device_address_txt

        init {
            view.setOnClickListener {
                onDeviceSelected(this.device)
            }
        }

        override fun bind(item: BluetoothDevice) {
            this.device = item

            this.nameText.text = if (AppUtils.stringNotEmpty(this.device.name)) {
                this.device.name
            } else {
                activity?.getString(R.string.unknown) ?: "No name"
            }

            this.addressText.text = this.device.address
        }

    }

}