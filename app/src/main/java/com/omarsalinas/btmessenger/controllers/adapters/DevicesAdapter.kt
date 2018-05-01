package com.omarsalinas.btmessenger.controllers.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.omarsalinas.btmessenger.R
import com.omarsalinas.btmessenger.common.AppUtils
import com.omarsalinas.btmessenger.common.SimpleAdapter
import com.omarsalinas.btmessenger.common.SimpleHolder
import com.omarsalinas.btmessenger.models.User
import kotlinx.android.synthetic.main.item_device.view.*

class DevicesAdapter(
        private val activity: Activity?,
        private val onDeviceSelected: (user: User) -> Unit
) : SimpleAdapter<User, DevicesAdapter.DevicesHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevicesHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)
        return DevicesHolder(view)
    }

    inner class DevicesHolder(view: View) : SimpleHolder<User>(view) {

        private lateinit var user: User

        private val nameText: TextView = view.item_device_name_txt
        private val addressText: TextView = view.item_device_address_txt

        init {
            view.setOnClickListener {
                onDeviceSelected(this.user)
            }
        }

        override fun bind(item: User) {
            this.user = item

            this.nameText.text = if (AppUtils.stringNotEmpty(this.user.userName)) this.user.userName
            else activity?.getString(R.string.unknown)

            this.addressText.text = this.user.address
        }

    }

}