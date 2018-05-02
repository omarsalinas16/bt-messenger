package com.omarsalinas.btmessenger.controllers

import android.content.Context
import android.content.Intent
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import com.omarsalinas.btmessenger.R
import com.omarsalinas.btmessenger.common.FragmentActivity
import com.omarsalinas.btmessenger.models.Device
import org.jetbrains.annotations.NotNull

class MainActivity : FragmentActivity(), DevicesFragment.Callbacks {

    companion object {
        private const val EXTRA_CURRENT_DEVICE: String = "com.omarsalinas.btmessenger.extra_current_device"

        fun newIntent(context: Context?, @NonNull @NotNull currentDevice: Device): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(EXTRA_CURRENT_DEVICE, currentDevice)

            return intent
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun createFragment(): Fragment {
        return DevicesFragment.newInstance()
    }

    override fun onDeviceSelected(palDevice: Device) {
        val currentDevice = intent.getParcelableExtra<Device>(EXTRA_CURRENT_DEVICE)

        if (getLayoutId() == R.layout.activity_double) {
            val fragment = ConversationFragment.newInstance(currentDevice, palDevice)

            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_extra, fragment)
                    .commit()
        } else {
            val intent = ConversationActivity.newIntent(this, currentDevice, palDevice)
            startActivity(intent)
        }
    }
}