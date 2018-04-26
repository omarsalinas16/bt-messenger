package com.omarsalinas.btmessenger.controllers

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import com.omarsalinas.btmessenger.R
import com.omarsalinas.btmessenger.common.FragmentActivity
import com.omarsalinas.btmessenger.dialogs.PermissionErrorDialog

class LoginActivity : FragmentActivity() {

    companion object {
        private const val TAG: String = "LOGIN_ACTIVITY"
        private const val REQUEST_CODE_ASK_PERMISSIONS: Int = 0x0001

        @JvmStatic
        private val REQUIRED_SDK_PERMISSIONS: Array<String> = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
        )
    }

    override fun createFragment(): Fragment = LoginFragment()

    /**
     * Requests the necessary permissions defined in [REQUIRED_SDK_PERMISSIONS]
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val missingPermissions = REQUIRED_SDK_PERMISSIONS.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }

            if (!missingPermissions.isEmpty()) {
                ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), REQUEST_CODE_ASK_PERMISSIONS)
            }
        }
    }

    /**
     * Returns a [PermissionErrorDialog] displaying an error message when the permission were not accepted,
     * the positive button closes the app.
     * @return The newly created [PermissionErrorDialog]
     */
    private fun getPermissionErrorDialog(): PermissionErrorDialog {
        return PermissionErrorDialog.newInstance {
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
                Log.d("REE", "permissions")
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
