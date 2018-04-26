package com.omarsalinas.btmessenger.dialogs

import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import com.omarsalinas.btmessenger.R
import com.omarsalinas.btmessenger.common.SimpleDialog

class PermissionErrorDialog : SimpleDialog() {

    companion object {
        private const val TAG: String = "PERMISSION_ERROR_DIALOG"

        fun newInstance(onAction: () -> Unit): PermissionErrorDialog {
            val dialog = PermissionErrorDialog()
            dialog.onAction = onAction

            return dialog
        }
    }

    var onAction: () -> Unit = { }

    override fun setup(dialog: AlertDialog.Builder) {
        dialog.setTitle(R.string.error_missing_permissions)
                .setMessage(R.string.error_missing_permissions_message)
                .setPositiveButton(android.R.string.ok) { d: DialogInterface?, _: Int ->
                    run {
                        d?.dismiss()
                        onAction()
                    }
                }

        this.isCancelable = false
    }

}