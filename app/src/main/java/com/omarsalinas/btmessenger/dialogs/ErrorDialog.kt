package com.omarsalinas.btmessenger.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.omarsalinas.btmessenger.common.SimpleDialog

class ErrorDialog : SimpleDialog() {

    companion object {
        private const val TAG: String = "PERMISSION_ERROR_DIALOG"
        private const val BUNDLE_ERROR_DIALOG_TITLE: String = "com.omarsalinas.btmessenger.bundle_error_dialog_title"
        private const val BUNDLE_ERROR_DIALOG_MESSAGE: String = "com.omarsalinas.btmessenger.bundle_error_dialog_message"

        fun newInstance(title: String, message: String, onAction: () -> Unit): ErrorDialog {
            val bundle = Bundle()
            bundle.putString(BUNDLE_ERROR_DIALOG_TITLE, title)
            bundle.putString(BUNDLE_ERROR_DIALOG_MESSAGE, message)

            val dialog = ErrorDialog()
            dialog.arguments = bundle
            dialog.onAction = onAction

            return dialog
        }
    }

    var onAction: () -> Unit = { }

    override fun setup(dialog: AlertDialog.Builder) {
        val title = this.arguments?.getString(BUNDLE_ERROR_DIALOG_TITLE)
        val message = this.arguments?.getString(BUNDLE_ERROR_DIALOG_MESSAGE)

        dialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok) { d: DialogInterface?, _: Int ->
                    run {
                        d?.dismiss()
                        onAction()
                    }
                }

        this.isCancelable = false
    }

}