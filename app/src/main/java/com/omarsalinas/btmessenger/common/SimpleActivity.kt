package com.omarsalinas.btmessenger.common

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity

abstract class SimpleActivity : AppCompatActivity() {

    companion object {
        private const val TAG: String = "SIMPLE_ACTIVITY"
    }

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

}