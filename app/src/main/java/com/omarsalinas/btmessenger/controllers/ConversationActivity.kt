package com.omarsalinas.btmessenger.controllers

import android.content.Context
import android.content.Intent
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import com.omarsalinas.btmessenger.common.FragmentActivity
import com.omarsalinas.btmessenger.models.User
import org.jetbrains.annotations.NotNull

class ConversationActivity : FragmentActivity() {

    companion object {
        private const val TAG: String = "CONVERSATION_ACTIVITY"
        private const val EXTRA_USER: String = "com.omarsalinas.btmessenger.extra_user"
        private const val EXTRA_PAL: String = "com.omarsalinas.btmessenger.extra_pal"

        fun newIntent(context: Context?, @NonNull @NotNull user: User, @NonNull @NotNull pal: User): Intent {
            val intent = Intent(context, ConversationActivity::class.java)
            intent.putExtra(EXTRA_USER, user)
            intent.putExtra(EXTRA_PAL, pal)

            return intent
        }
    }

    override fun createFragment(): Fragment {
        val user = intent.getParcelableExtra<User>(EXTRA_USER)
        val pal = intent.getParcelableExtra<User>(EXTRA_PAL)

        return ConversationFragment.newInstance(user, pal)
    }
}