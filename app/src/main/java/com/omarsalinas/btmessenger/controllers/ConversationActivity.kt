package com.omarsalinas.btmessenger.controllers

import android.content.Context
import android.content.Intent
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import com.omarsalinas.btmessenger.common.FragmentActivity
import com.omarsalinas.btmessenger.models.Device
import org.jetbrains.annotations.NotNull

class ConversationActivity : FragmentActivity() {

    companion object {
        private const val EXTRA_CURRENT_DEVICE: String = "com.omarsalinas.btmessenger.extra_current_device"
        private const val EXTRA_PAL_DEVICE: String = "com.omarsalinas.btmessenger.extra_pal_device"

        fun newIntent(context: Context?, @NonNull @NotNull currentDevice: Device, @NonNull @NotNull pal: Device): Intent {
            val intent = Intent(context, ConversationActivity::class.java)
            intent.putExtra(EXTRA_CURRENT_DEVICE, currentDevice)
            intent.putExtra(EXTRA_PAL_DEVICE, pal)

            return intent
        }
    }

    override fun createFragment(): Fragment {
        val user = intent.getParcelableExtra<Device>(EXTRA_CURRENT_DEVICE)
        val pal = intent.getParcelableExtra<Device>(EXTRA_PAL_DEVICE)

        return ConversationFragment.newInstance(user, pal)
    }
}