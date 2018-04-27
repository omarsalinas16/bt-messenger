package com.omarsalinas.btmessenger.controllers

import android.os.Bundle
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.AppCompatImageButton
import android.support.v7.widget.RecyclerView
import android.view.View
import com.omarsalinas.btmessenger.R
import com.omarsalinas.btmessenger.common.SimpleFragment
import kotlinx.android.synthetic.main.fragment_conversation.view.*

class ConversationFragment : SimpleFragment() {

    companion object {
        private const val TAG: String = "CONVERSATION_FRAGMENT"
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageEditText: AppCompatEditText
    private lateinit var sendButton: AppCompatImageButton

    override fun getLayoutId(): Int = R.layout.fragment_conversation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewsById(view)
    }

    private fun setViewsById(view: View) {
        this.recyclerView = view.fragment_conversation_rv
        this.messageEditText = view.fragment_conversation_message_et
        this.sendButton = view.fragment_conversation_send_btn
    }

}