package com.omarsalinas.btmessenger.controllers

import android.content.DialogInterface
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.omarsalinas.btmessenger.R
import com.omarsalinas.btmessenger.common.BtHelper
import com.omarsalinas.btmessenger.common.SimpleFragment
import com.omarsalinas.btmessenger.models.User
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragment : SimpleFragment() {

    companion object {
        private const val TAG: String = "LOGIN_FRAGMENT"
        private const val PREFS_USERNAME = "com.omarsalinas.btmessenger.prefs_username"
    }

    private var savedUserName: String = ""

    private lateinit var userNameEditText: EditText
    private lateinit var enterButton: Button

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
                doLogin()
            }

            false
        }

        this.enterButton = view.fragment_login_enter_btn
        this.enterButton.setOnClickListener { doLogin() }

        this.savedUserName = loadSavedUsername()

        if (this.savedUserName.isNotBlank() && this.savedUserName.isNotEmpty()) {
            this.userNameEditText.setText(this.savedUserName)
        }
    }

    private fun doLogin() {
        val userName = getUserName()
        val btHelper = BtHelper()
        val user = User(userName, btHelper.address)

        if (userName != this.savedUserName) {
            getSaveUsernameDialog(user).show()
        } else {
            openDevicesActivity(user)
        }
    }

    private fun openDevicesActivity(user: User) {
        val intent = DevicesActivity.newIntent(this.context, user)
        startActivity(intent)
    }

    private fun getUserName(): String {
        return this.userNameEditText.text.toString()
    }

    private fun setButtonActive(active: Boolean) {
        this.enterButton.isClickable = active
        this.enterButton.alpha = if (active) 1.0f else 0.45f
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

    private fun getSaveUsernameDialog(user: User): AlertDialog.Builder {
        return AlertDialog.Builder(this.activity!!)
                .setTitle(R.string.fragment_login_save_username)
                .setMessage(this.activity!!.getString(R.string.fragment_login_save_username_message, user.userName))
                .setPositiveButton(android.R.string.ok) { dialog: DialogInterface?, _: Int ->
                    run {
                        saveUsernameToPrefs(user.userName)
                        openDevicesActivity(user)
                        dialog?.dismiss()
                    }
                }
                .setNegativeButton(android.R.string.no) { dialog: DialogInterface?, _: Int ->
                    run {
                        dialog?.cancel()
                    }
                }
                .setOnCancelListener {
                    openDevicesActivity(user)
                }
                .setCancelable(true)
    }

    private inner class UsernameEditTextWatcher : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            setButtonActive(s.isNotBlank() && s.isNotEmpty())
        }

    }

}