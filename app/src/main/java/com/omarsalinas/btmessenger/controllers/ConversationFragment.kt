package com.omarsalinas.btmessenger.controllers

import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v7.widget.*
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.omarsalinas.btmessenger.R
import com.omarsalinas.btmessenger.common.AppUtils
import com.omarsalinas.btmessenger.common.SimpleFragment
import com.omarsalinas.btmessenger.controllers.adapters.ConversationAdapter
import com.omarsalinas.btmessenger.models.Message
import com.omarsalinas.btmessenger.models.User
import kotlinx.android.synthetic.main.fragment_conversation.view.*
import org.jetbrains.annotations.NotNull
import java.util.*
import kotlin.concurrent.schedule

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
    private lateinit var palNameText: AppCompatTextView
    private lateinit var palAddressText: AppCompatTextView

    override fun getLayoutId(): Int = R.layout.fragment_conversation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.user = arguments!!.getParcelable(BUNDLE_USER)
        this.pal = arguments!!.getParcelable(BUNDLE_PAL)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.adapter = ConversationAdapter(this.user)
        setViewsById(view)

        setPalInfo(this.pal)

        addMessage(Message("Hi", this.pal))
        addMessage(Message("This is a test", this.pal))

        addMessage(Message("Hello", this.user))

        addMessage(Message("Lorem ipsum", this.pal))

        addMessage(Message("Dolor sit", this.user))
        addMessage(Message("Amet", this.user))

        Timer("MESSAGE", false).schedule(60000) {
            activity?.runOnUiThread {
                addMessage(Message("Hello, I'm back after one minute", user))
            }
        }
    }

    private fun setViewsById(view: View) {
        this.recyclerView = view.fragment_conversation_rv
        this.messageEditText = view.fragment_conversation_message_et
        this.sendButton = view.fragment_conversation_send_btn
        this.palNameText = view.fragment_conversation_pal_name_txt
        this.palAddressText = view.fragment_conversation_pal_address_txt

        this.messageEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                AppUtils.setButtonActive(sendButton, AppUtils.stringNotEmpty(s))
            }
        })

        val layoutManager = LinearLayoutManager(this.context)
        layoutManager.stackFromEnd = true

        this.recyclerView.layoutManager = layoutManager
        this.recyclerView.adapter = this.adapter

        this.sendButton.setOnClickListener {
            onSendButtonClicked()
        }

        AppUtils.setButtonActive(sendButton, false)
    }

    private fun onSendButtonClicked() {
        addMessage(createMessage())
        this.messageEditText.setText("")
    }

    private fun createMessage(): Message {
        return Message(AppUtils.getEditTextValue(this.messageEditText), this.user)
    }

    private fun addMessage(message: Message) {
        this.adapter.add(message)
        this.recyclerView.smoothScrollToPosition(this.adapter.itemCount - 1)
    }

    private fun setPalInfo(pal: User) {
        setPalInfo(pal.userName, pal.btAddress)
    }

    private fun setPalInfo(userName: String, address: String) {
        this.palNameText.text = if (AppUtils.stringNotEmpty(userName)) userName else "Unknown"
        this.palAddressText.text = address
    }

}