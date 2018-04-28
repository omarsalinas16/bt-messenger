package com.omarsalinas.btmessenger.controllers

import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.AppCompatImageButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.omarsalinas.btmessenger.R
import com.omarsalinas.btmessenger.common.AppUtils
import com.omarsalinas.btmessenger.common.SimpleFragment
import com.omarsalinas.btmessenger.controllers.adapters.ConversationAdapter
import com.omarsalinas.btmessenger.models.Message
import com.omarsalinas.btmessenger.models.User
import kotlinx.android.synthetic.main.fragment_conversation.view.*
import org.jetbrains.annotations.NotNull

class ConversationFragment : SimpleFragment() {

    companion object {
        private const val TAG: String = "CONVERSATION_FRAGMENT"
        private const val BUNDLE_USER = "com.omarsalinas.btmessenger.bundle_user"
        private const val BUNDLE_PAL = "com.omarsalinas.btmessenger.bundle_pal"

        fun newInstance(@NonNull @NotNull user: User, @NonNull @NotNull pal: User): ConversationFragment {
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_USER, user)
            bundle.putParcelable(BUNDLE_PAL, pal)

            val fragment = ConversationFragment()
            fragment.arguments = bundle

            return fragment
        }
    }

    private lateinit var user: User
    private lateinit var pal: User
    private lateinit var adapter: ConversationAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageEditText: AppCompatEditText
    private lateinit var sendButton: AppCompatImageButton

    override fun getLayoutId(): Int = R.layout.fragment_conversation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.user = arguments!!.getParcelable(BUNDLE_USER)
        this.pal = arguments!!.getParcelable(BUNDLE_PAL)

        this.activity?.title = this.pal.userName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.adapter = ConversationAdapter(this.user)

        this.adapter.add(Message("Hi", this.pal))
        this.adapter.add(Message("This is a test", this.pal))

        setViewsById(view)
    }

    private fun setViewsById(view: View) {
        this.recyclerView = view.fragment_conversation_rv
        this.messageEditText = view.fragment_conversation_message_et
        this.sendButton = view.fragment_conversation_send_btn

        val layoutManager = LinearLayoutManager(this.context)
        layoutManager.stackFromEnd = true

        this.recyclerView.layoutManager = layoutManager
        this.recyclerView.adapter = this.adapter

        this.sendButton.setOnClickListener {
            onSendButtonClicked()
        }
    }

    private fun onSendButtonClicked() {
        sendMessage(createMessage())
    }

    private fun createMessage(): Message {
        return Message(AppUtils.getEditTextValue(this.messageEditText), this.user)
    }

    private fun sendMessage(message: Message) {
        this.adapter.add(message)
        this.recyclerView.smoothScrollToPosition(this.adapter.itemCount - 1)
    }

}