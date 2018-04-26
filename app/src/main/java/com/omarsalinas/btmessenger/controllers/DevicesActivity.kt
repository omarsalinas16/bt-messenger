package com.omarsalinas.btmessenger.controllers

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import com.omarsalinas.btmessenger.common.FragmentActivity
import com.omarsalinas.btmessenger.models.User

class DevicesActivity : FragmentActivity() {

    companion object {
        private const val TAG: String = "DEVICES_ACTIVITY"
        private const val EXTRA_USER: String = "com.omarsalinas.btmessenger.extra_user"

        fun newIntent(context: Context?, user: User): Intent {
            val intent = Intent(context, DevicesActivity::class.java)
            intent.putExtra(EXTRA_USER, user)

            return intent
        }
    }

    override fun createFragment(): Fragment {
        val user = intent.getParcelableExtra<User>(EXTRA_USER)
        return DevicesFragment.newInstance(user)
    }

}