package com.omarsalinas.btmessenger.common

import android.os.Bundle
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

        val fm = supportFragmentManager
        var fragment = fm.findFragmentById(R.id.fragment_container)

        if (fragment == null) {
            fragment = createFragment()
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit()
        }
    }

}