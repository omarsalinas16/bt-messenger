package com.omarsalinas.btmessenger.common

import android.support.annotation.Nullable
import android.widget.Button
import android.widget.EditText

object AppUtils {

    fun stringNotEmpty(@Nullable string: CharSequence?): Boolean {
        return !string.isNullOrBlank() && !string.isNullOrEmpty()
    }

    fun setButtonActive(@Nullable button: Button?, active: Boolean) {
        button?.isClickable = active
        button?.alpha = if (active) 1.0f else 0.45f
    }

    fun getEditTextValue(@Nullable editText: EditText?): String {
        return editText?.text?.toString() ?: ""
    }

}