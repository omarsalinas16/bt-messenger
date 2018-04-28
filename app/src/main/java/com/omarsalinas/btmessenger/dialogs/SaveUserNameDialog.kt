package com.omarsalinas.btmessenger.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.omarsalinas.btmessenger.R
import com.omarsalinas.btmessenger.common.SimpleDialog

class SaveUserNameDialog : SimpleDialog() {

    companion object {
        private const val TAG: String = "SAVE_USERNAME_DIALOG"
        private const val BUNDLE_USERNAME = "com.omarsalinas.btmessenger.bundle_username"

        fun newInstance(userName: String, onPositive: (dialog: DialogInterface?) -> Unit, onCancel: () -> Unit): SaveUserNameDialog {
            val bundle = Bundle()
            bundle.putString(BUNDLE_USERNAME, userName)

            val dialog = SaveUserNameDialog()
            dialog.arguments = bundle

            dialog.onPositive = onPositive
            dialog.onCancel = onCancel

            return dialog
        }
    }

    var onPositive: (dialog: DialogInterface?) -> Unit = { }
    var onCancel: () -> Unit = { }

    override fun setup(dialog: AlertDialog.Builder) {
        val userName: String = arguments?.getString(BUNDLE_USERNAME) ?: ""

        dialog.setTitle(R.string.fragment_login_save_username)
                .setMessage(this.activity!!.getString(R.string.fragment_login_save_username_message, userName))
                .setPositiveButton(android.R.string.ok) { d: DialogInterface?, _: Int ->
                    run {
                        d?.dismiss()
                        onPositive(d)
                    }
                }
                .setNegativeButton(android.R.string.no) { d: DialogInterface?, _: Int ->
                    run {
                        d?.cancel()
                    }
                }

        this.isCancelable = true
    }

    override fun onCancel(dialog: DialogInterface?) {
        onCancel()
    }

}