package com.omarsalinas.btmessenger.common

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

abstract class SimpleActivity : AppCompatActivity() {

    /**
     * Returns the layout resource id to be set as the content view.
     * @returns The layout resource
     */
    @LayoutRes
    protected abstract fun getLayoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
    }

    protected fun toast(@StringRes id: Int, length: Int) {
        Toast.makeText(this, id, length).show()
    }

    protected fun toast(message: String, length: Int) {
        Toast.makeText(this, message, length).show()
    }

}