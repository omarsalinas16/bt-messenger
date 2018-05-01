package com.omarsalinas.btmessenger.controllers

import android.Manifest
import android.bluetooth.BluetoothAdapter
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
import android.view.KeyEvent
import com.omarsalinas.btmessenger.R
import com.omarsalinas.btmessenger.common.AppUtils
import com.omarsalinas.btmessenger.common.BtHelper
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

    private var btAdapter: BluetoothAdapter? = null

    private lateinit var userNameEditText: AppCompatEditText
    private lateinit var enterButton: AppCompatButton

    override fun getLayoutId(): Int = R.layout.activity_login

    /**
     * Requests the necessary permissions defined in [REQUIRED_SDK_PERMISSIONS]
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            this.btAdapter = BluetoothAdapter.getDefaultAdapter()

            if (this.btAdapter == null) {
                AppUtils.getNoBluetoothErrorDialog(this).show(this.supportFragmentManager)
                return
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val missingPermissions = REQUIRED_SDK_PERMISSIONS.filter {
                    ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
                }

                if (!missingPermissions.isEmpty()) {
                    ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), REQUEST_CODE_ASK_PERMISSIONS)
                }
            }

            setViewsById()
            setViewListeners()

            val savedUserName = loadSavedUserName()

            if (AppUtils.stringNotEmpty(savedUserName)) {
                this.userNameEditText.setText(savedUserName)
            } else {
                val deviceName = this.btAdapter?.name
                if (AppUtils.stringNotEmpty(deviceName)) this.userNameEditText.setText(deviceName)
            }
        } else {
            AppUtils.getNoBluetoothErrorDialog(this).show(this.supportFragmentManager)
        }
    }

    /**
     * Load view widgets
     */
    private fun setViewsById() {
        this.userNameEditText = this.activity_login_username_et
        this.enterButton = this.activity_login_enter_btn
    }

    /**
     * Set view widgets listeners
     */
    private fun setViewListeners() {
        this.userNameEditText.apply {
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    AppUtils.setButtonActive(enterButton, AppUtils.stringNotEmpty(s))
                }
            })

            setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (AppUtils.stringNotEmpty(this.text)) {
                        doLogin()
                    }
                }

                false
            }

            clearFocus()
        }

        this.enterButton.setOnClickListener { doLogin() }
    }

    /**
     * Initialize the login process, if the input username is different than the saved one at prefs
     * then ask if the user wants to set it as its default. If the device has no Bluetooth or the
     * adapter failed to set then display an error message.
     */
    private fun doLogin() {
        val userName = AppUtils.getEditTextValue(this.userNameEditText)
        val address = BtHelper.getAddress(this)

        if (AppUtils.stringNotEmpty(userName) && AppUtils.stringNotEmpty(address)) {
            val user = User(userName, address)

            if (userName != loadSavedUserName()) {
                getSaveUserNameDialog(user).show(this.supportFragmentManager)
            } else {
                openMainActivity(user)
            }
        } else {
            AppUtils.getNoBluetoothErrorDialog(this).show(this.supportFragmentManager)
        }
    }

    private fun openMainActivity(user: User) {
        val intent = MainActivity.newIntent(this, user)
        startActivity(intent)
    }

    private fun loadSavedUserName(): String {
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
