package com.omarsalinas.btmessenger.controllers.adapters

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.omarsalinas.btmessenger.R
import kotlinx.android.synthetic.main.item_device.view.*

class DevicesAdapter(
        private val activity: Activity?,
        val onDeviceSelected: (device: BluetoothDevice) -> Unit
) : RecyclerView.Adapter<DevicesAdapter.DevicesHolder>() {

    private var list: ArrayList<BluetoothDevice> = arrayListOf()

    override fun getItemCount(): Int = this.list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevicesHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)
        return DevicesHolder(view)
    }

    override fun onBindViewHolder(holder: DevicesHolder, position: Int) {
        holder.bind(this.list[position])
    }

    fun add(device: BluetoothDevice): Boolean {
        return if (!this.list.contains(device)) {
            this.list.add(device)
            notifyItemInserted(this.list.size - 1)

            true
        } else {
            false
        }
    }

    fun clear() {
        this.list.clear()
        notifyDataSetChanged()
    }

    inner class DevicesHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var device: BluetoothDevice

        private val nameText: TextView = view.item_device_name_txt
        private val addressText: TextView = view.item_device_address_txt

        fun bind(device: BluetoothDevice) {
            this.device = device

            this.nameText.text = if (!this.device.name.isNullOrEmpty() && this.device.name.isNotBlank()){
                this.device.name
            } else {
                activity?.getString(R.string.unknown) ?: "No name"
            }

            this.addressText.text = this.device.address

            this.view.setOnClickListener {
                onDeviceSelected(this.device)
            }
        }

    }

}