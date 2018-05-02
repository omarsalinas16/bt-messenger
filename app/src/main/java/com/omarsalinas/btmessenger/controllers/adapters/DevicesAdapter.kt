package com.omarsalinas.btmessenger.controllers.adapters

import android.support.v7.widget.AppCompatTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.omarsalinas.btmessenger.R
import com.omarsalinas.btmessenger.common.AppUtils
import com.omarsalinas.btmessenger.common.SimpleAdapter
import com.omarsalinas.btmessenger.common.SimpleHolder
import com.omarsalinas.btmessenger.models.Device
import kotlinx.android.synthetic.main.item_device.view.*
import java.util.ArrayList

class DevicesAdapter(
        private val callbacks: Callbacks,
        getter: () -> ArrayList<Device>
) : SimpleAdapter<Device, DevicesAdapter.DevicesHolder>(getter) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevicesHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)
        return DevicesHolder(view)
    }

    inner class DevicesHolder(view: View) : SimpleHolder<Device>(view) {

        private lateinit var device: Device

        private val nameText: AppCompatTextView = view.item_device_name_txt
        private val addressText: AppCompatTextView = view.item_device_address_txt
        private val pairedText: AppCompatTextView = view.item_device_paired_txt

        init {
            view.setOnClickListener {
                callbacks.onDeviceSelected(this.device)
            }
        }

        override fun bind(item: Device) {
            this.device = item

            this.nameText.text = this.device.name
            this.addressText.text = this.device.address

            if (item.paired) AppUtils.setVisibility(this.pairedText, true)
        }

    }

    interface Callbacks {
        fun onDeviceSelected(device: Device)
    }

}