package com.omarsalinas.btmessenger.controllers

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import com.omarsalinas.btmessenger.R
import com.omarsalinas.btmessenger.common.AppUtils
import com.omarsalinas.btmessenger.common.BtHelper
import com.omarsalinas.btmessenger.common.BtHelperException
import com.omarsalinas.btmessenger.common.SimpleActivity
import com.omarsalinas.btmessenger.dialogs.ErrorDialog
import com.omarsalinas.btmessenger.dialogs.SaveUserNameDialog
import com.omarsalinas.btmessenger.models.User
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : SimpleActivity() {

    companion object {
        private const val TAG: String = "LOGIN_ACTIVITY"
        private const val REQUEST_CODE_ASK_PERMISSIONS: Int = 0x0001
        private const val PREFS_USERNAME = "com.omarsalinas.btmessenger.prefs_username"

        @JvmStatic
        private val REQUIRED_SDK_PERMISSIONS: Array<String> = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
        )
    }

    private var savedUserName: String = ""
    private lateinit var btHelper: BtHelper

    private lateinit var userNameEditText: AppCompatEditText
    private lateinit var enterButton: AppCompatButton

    override fun getLayoutId(): Int = R.layout.activity_login

    /**
     * Requests the necessary permissions defined in [REQUIRED_SDK_PERMISSIONS]
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            this.btHelper = BtHelper()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val missingPermissions = REQUIRED_SDK_PERMISSIONS.filter {
                    ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
                }

                if (!missingPermissions.isEmpty()) {
                    ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), REQUEST_CODE_ASK_PERMISSIONS)
                }
            }

            setViewsById()
        } else {
            AppUtils.getNoBluetoothErrorDialog(this).show(this.supportFragmentManager)
        }
    }

    /**
     * Load view widgets and set listeners
     */
    private fun setViewsById() {
        this.userNameEditText = this.activity_login_username_et

        this.userNameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                AppUtils.setButtonActive(enterButton, AppUtils.stringNotEmpty(s))
            }
        })

        this.userNameEditText.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                if (AppUtils.stringNotEmpty(this.userNameEditText.text)) {
                    doLogin()
                }
            }

            false
        }

        this.userNameEditText.clearFocus()

        this.enterButton = this.activity_login_enter_btn
        this.enterButton.setOnClickListener { doLogin() }

        this.savedUserName = loadSavedUsername()

        if (AppUtils.stringNotEmpty(this.savedUserName)) {
            this.userNameEditText.setText(this.savedUserName)
        } else {
            try {
                val deviceName = this.btHelper.name
                if (AppUtils.stringNotEmpty(deviceName)) this.userNameEditText.setText(deviceName)
            } catch (e: BtHelperException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Retrieve the saved username each time the activity is resumed to keep a real copy of the
     * actually saved data and not just the in-memory variable
     */
    override fun onResume() {
        super.onResume()
        this.savedUserName = loadSavedUsername()
    }

    /**
     * Initialize the login process, if the input username is different than the saved one at prefs
     * then ask if the user wants to set it as its default. If the device has no Bluetooth or the
     * adapter failed to set then display an error message.
     */
    private fun doLogin() {
        try {
            val userName = AppUtils.getEditTextValue(this.userNameEditText)
            val user = User(userName, this.btHelper.address)
            // val user = User(userName, "00:00:00:00:00:00")

            if (userName != this.savedUserName) {
                getSaveUserNameDialog(user).show(this.supportFragmentManager)
            } else {
                openMainActivity(user)
            }
        } catch (e: BtHelperException) {
            AppUtils.getNoBluetoothErrorDialog(this).show(this.supportFragmentManager)
        }
    }

    private fun openMainActivity(user: User) {
        val intent = MainActivity.newIntent(this, user)
        startActivity(intent)
    }

    private fun loadSavedUsername(): String {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(this)
        return preferenceManager.getString(PREFS_USERNAME, "") ?: ""
    }

    private fun saveUsernameToPrefs(userName: String) {
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putString(PREFS_USERNAME, userName)
                .apply()
    }

    /**
     * Returns a [SaveUserNameDialog] displaying the option to set the newly introduced username as
     * the default. Opens [MainActivity] regardless
     * @return The newly created [SaveUserNameDialog]
     */
    private fun getSaveUserNameDialog(user: User): SaveUserNameDialog {
        return SaveUserNameDialog.newInstance(
                user.userName,
                { _: DialogInterface? ->
                    saveUsernameToPrefs(user.userName)
                    openMainActivity(user)
                },
                { openMainActivity(user) }
        )
    }

    /**
     * Returns a [ErrorDialog] displaying an error message when the permission were not accepted,
     * the positive button closes the app.
     * @return The newly created [ErrorDialog]
     */
    private fun getPermissionErrorDialog(): ErrorDialog {
        val title = getString(R.string.error_missing_permissions)
        val message = getString(R.string.error_missing_permissions_message)

        return ErrorDialog.newInstance(title, message) {
            finishAndRemoveTask()
        }
    }

    /**
     * Check if all permissions in [REQUIRED_SDK_PERMISSIONS] have been accepted, otherwise
     * force close the application
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> {
                permissions.forEach {
                    if (grantResults[permissions.indexOf(it)] != PackageManager.PERMISSION_GRANTED) {
                        getPermissionErrorDialog().show(this.supportFragmentManager)
                        return
                    }
                }
            }
        }
    }

}
