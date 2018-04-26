package com.omarsalinas.btmessenger.common

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

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

}