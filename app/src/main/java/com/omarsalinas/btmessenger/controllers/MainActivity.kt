package com.omarsalinas.btmessenger.controllers

import android.content.Context
import android.content.Intent
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import android.util.Log
import android.widget.FrameLayout
import com.omarsalinas.btmessenger.R
import com.omarsalinas.btmessenger.common.FragmentActivity
import com.omarsalinas.btmessenger.models.User
import kotlinx.android.synthetic.main.activity_double.*
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

    override fun getLayoutId(): Int = R.layout.activity_double

    override fun createFragment(): Fragment {
        return DevicesFragment.newInstance()
    }

    override fun onDeviceSelected(pal: User) {
        val user = intent.getParcelableExtra<User>(EXTRA_USER)

        try {
            findViewById<FrameLayout>(R.id.fragment_container_extra)

            val fragment = ConversationFragment.newInstance(user, pal)

            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_extra, fragment)
                    .commit()
        } catch (e: Exception) {
            val intent = ConversationActivity.newIntent(this, user, pal)
            startActivity(intent)
        }
    }
}