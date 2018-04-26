package com.omarsalinas.btmessenger.common

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog

abstract class SimpleDialog : DialogFragment() {

    companion object {
        private const val TAG: String = "SIMPLE_DIALOG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.retainInstance = true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(this.context!!)
        setup(dialog)

        return dialog.create()
    }

    /**
     * Configures the generated [AlertDialog] before returning it in the [onCreateDialog] method
     * @param dialog The [AlertDialog.Builder] to configure
     */
    abstract fun setup(dialog: AlertDialog.Builder)

    /**
     * Wrapper method to call show with the tag already set
     */
    fun show(manager: FragmentManager?) {
        show(manager, TAG)
    }

}