package com.omarsalinas.btmessenger.common

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import com.omarsalinas.btmessenger.R

abstract class FragmentActivity : SimpleActivity() {

    companion object {
        private const val TAG: String = "FRAGMENT_ACTIVITY"
    }

    override fun getLayoutId(): Int = R.layout.activity_fragment
    protected abstract fun createFragment(): Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentToContainer(R.id.fragment_container) {
            createFragment()
        }
    }

    private fun setFragmentToContainer(@IdRes id: Int, getFragment: () -> Fragment) {
        val fm = supportFragmentManager
        var fragment = fm.findFragmentById(id)

        if (fragment == null) {
            fragment = getFragment()
            fm.beginTransaction().add(id, fragment).commit()
        }
    }

}