package com.omarsalinas.btmessenger.common

import android.app.Activity
import android.support.annotation.NonNull
import android.support.annotation.Nullable
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.omarsalinas.btmessenger.R
import com.omarsalinas.btmessenger.dialogs.ErrorDialog
import org.jetbrains.annotations.NotNull

object AppUtils {

    fun stringNotEmpty(@Nullable string: CharSequence?): Boolean {
        return !string.isNullOrBlank() && !string.isNullOrEmpty()
    }

    fun setVisibility(@Nullable view: View?, visible: Boolean) {
        view?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setButtonActive(@Nullable button: Button?, active: Boolean) {
        button?.isClickable = active
        button?.alpha = if (active) 1.0f else 0.45f
    }

    fun setButtonActive(@Nullable button: ImageButton?, active: Boolean) {
        button?.isClickable = active
        button?.alpha = if (active) 1.0f else 0.45f
    }

    fun getEditTextValue(@Nullable editText: EditText?): String {
        return editText?.text?.toString() ?: ""
    }

    fun getNoBluetoothErrorDialog(@NonNull @NotNull activity: Activity): ErrorDialog {
        val title = activity.getString(R.string.error_no_bluetooth)
        val message = activity.getString(R.string.error_no_bluetooth_message)

        return ErrorDialog.newInstance(title, message) {
            activity.finishAndRemoveTask()
        }
    }

    fun startTransition(group: ViewGroup) {
        try {
            TransitionManager.beginDelayedTransition(group)
        } catch (e: Exception) {
            Log.e("START_TRANSITION", "Ignored transition, error: $e")
        }
    }

}