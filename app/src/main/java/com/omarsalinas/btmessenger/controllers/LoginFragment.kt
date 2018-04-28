package com.omarsalinas.btmessenger.controllers

import android.content.DialogInterface
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import com.omarsalinas.btmessenger.R
import com.omarsalinas.btmessenger.common.BtHelper
import com.omarsalinas.btmessenger.common.AppUtils
import com.omarsalinas.btmessenger.common.SimpleFragment
import com.omarsalinas.btmessenger.dialogs.ErrorDialog
import com.omarsalinas.btmessenger.dialogs.SaveUserNameDialog
import com.omarsalinas.btmessenger.models.User
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragment : SimpleFragment() {

    companion object {
        private const val TAG: String = "LOGIN_FRAGMENT"
        private const val PREFS_USERNAME = "com.omarsalinas.btmessenger.prefs_username"
    }

    private var savedUserName: String = ""

    private lateinit var userNameEditText: AppCompatEditText
    private lateinit var enterButton: AppCompatButton

    override fun getLayoutId(): Int = R.layout.fragment_login

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewsById(view)
    }

    private fun setViewsById(view: View) {
        this.userNameEditText = view.fragment_login_username_et
        this.userNameEditText.addTextChangedListener(UsernameEditTextWatcher())
        this.userNameEditText.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                if (AppUtils.stringNotEmpty(this.userNameEditText.text)) {
                    doLogin()
                }
            }

            false
        }

        this.enterButton = view.fragment_login_enter_btn
        this.enterButton.setOnClickListener { doLogin() }

        this.savedUserName = loadSavedUsername()
        this.userNameEditText.setText(this.savedUserName)
    }

    override fun onResume() {
        super.onResume()
        this.savedUserName = loadSavedUsername()
    }

    private fun doLogin() {
        try {
            // val btHelper = BtHelper()

            val userName = AppUtils.getEditTextValue(this.userNameEditText)
            // val user = User(userName, btHelper.address)
            val user = User(userName, "00:00:00:00:00:00")

            if (userName != this.savedUserName) {
                getSaveUserNameDialog(user).show(this.fragmentManager)
            } else {
                openDevicesActivity(user)
            }
        } catch (e: NullPointerException) {
            AppUtils.getNoBluetoothErrorDialog(this.activity!!).show(this.fragmentManager)
        }
    }

    private fun openDevicesActivity(user: User) {
        val intent = MainActivity.newIntent(this.context, user)
        startActivity(intent)
    }

    private fun loadSavedUsername(): String {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(this.activity)
        return preferenceManager.getString(PREFS_USERNAME, "") ?: ""
    }

    private fun saveUsernameToPrefs(userName: String) {
        PreferenceManager.getDefaultSharedPreferences(this.activity).edit()
                .putString(PREFS_USERNAME, userName)
                .apply()
    }

    private fun getSaveUserNameDialog(user: User): SaveUserNameDialog {
        return SaveUserNameDialog.newInstance(
                user.userName,
                { _: DialogInterface? ->
                    saveUsernameToPrefs(user.userName)
                    openDevicesActivity(user)
                },
                {
                    Log.d(TAG, "cancel dialog")
                    openDevicesActivity(user)
                }
        )
    }

    private inner class UsernameEditTextWatcher : TextWatcher {

        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            AppUtils.setButtonActive(enterButton, AppUtils.stringNotEmpty(s))
        }

    }

}