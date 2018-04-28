package com.omarsalinas.btmessenger.controllers

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import android.widget.FrameLayout
import android.widget.Toast
import com.omarsalinas.btmessenger.R
import com.omarsalinas.btmessenger.common.FragmentActivity
import com.omarsalinas.btmessenger.models.User
import org.jetbrains.annotations.NotNull

class MainActivity : FragmentActivity(), DevicesFragment.Callbacks {

    companion object {
        private const val TAG: String = "DEVICES_ACTIVITY"
        private const val EXTRA_USER: String = "com.omarsalinas.btmessenger.extra_user"

        fun newIntent(context: Context?, @NonNull @NotNull user: User): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(EXTRA_USER, user)

            return intent
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun createFragment(): Fragment {
        val user = intent.getParcelableExtra<User>(EXTRA_USER)
        return DevicesFragment.newInstance(user)
    }

    override fun onDeviceSelected(device: BluetoothDevice) {
        val user = intent.getParcelableExtra<User>(EXTRA_USER)
        val pal = User(device.name ?: "Buddy", device.address)

        if (findViewById<FrameLayout>(R.id.fragment_container_extra) != null) {
            val intent = ConversationActivity.newIntent(this, user, pal)
            startActivity(intent)
        } else {
            val fragment = ConversationFragment.newInstance(user, pal)

            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_extra, fragment)
                    .commit()
        }
    }

}