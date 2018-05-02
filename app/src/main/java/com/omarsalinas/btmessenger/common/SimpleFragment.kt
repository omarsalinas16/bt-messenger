package com.omarsalinas.btmessenger.common

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

abstract class SimpleFragment : Fragment() {

    companion object {
        private const val TAG: String = "SIMPLE_FRAGMENT"
    }

    @LayoutRes
    protected abstract fun getLayoutId(): Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.retainInstance = true
        return inflater.inflate(getLayoutId(), container, false)
    }

    protected fun toast(@StringRes id: Int, length: Int) {
        this.activity?.let { Toast.makeText(this.activity, id, length).show() }
    }

    protected fun finish() {
        this.activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
    }

}