package com.omarsalinas.btmessenger.controllers

import android.os.Bundle
import com.omarsalinas.btmessenger.R
import com.omarsalinas.btmessenger.common.SimpleFragment
import com.omarsalinas.btmessenger.models.User

class DevicesFragment : SimpleFragment() {

    companion object {
        private const val TAG: String = "DEVICES_FRAGMENT"
        private const val BUNDLE_USER = "com.omarsalinas.btmessenger.bundle_user"

        fun newInstance(user: User): DevicesFragment {
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_USER, user)

            val fragment = DevicesFragment()
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_devices

}