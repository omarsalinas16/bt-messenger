package com.omarsalinas.btmessenger.controllers

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Build
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.annotation.StringRes
import android.support.v7.widget.*
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionManager
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import com.omarsalinas.btmessenger.R
import com.omarsalinas.btmessenger.common.*
import com.omarsalinas.btmessenger.controllers.adapters.ConversationAdapter
import com.omarsalinas.btmessenger.models.Message
import com.omarsalinas.btmessenger.models.Device
import kotlinx.android.synthetic.main.fragment_conversation.view.*
import org.jetbrains.annotations.NotNull
import java.util.*

class ConversationFragment : SimpleFragment(), BtHandler.Callbacks {

    companion object {
        private const val BUNDLE_USER = "com.omarsalinas.btmessenger.bundle_user"
        private const val BUNDLE_PAL = "com.omarsalinas.btmessenger.bundle_pal"

        fun newInstance(@NonNull @NotNull currentDevice: Device, @NonNull @NotNull pal: Device): ConversationFragment {
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_USER, currentDevice)
            bundle.putParcelable(BUNDLE_PAL, pal)

            val fragment = ConversationFragment()
            fragment.arguments = bundle

            return fragment
        }
    }

    private var messages: ArrayList<Message> = arrayListOf()

    private lateinit var currentdevice: Device
    private lateinit var pal: Device
    private lateinit var adapter: ConversationAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageEditText: AppCompatEditText
    private lateinit var sendButton: AppCompatImageButton

    private lateinit var palNameText: AppCompatTextView
    private lateinit var palAddressText: AppCompatTextView

    private lateinit var loadingContainer: LinearLayout
    private lateinit var loading: ProgressBar
    private lateinit var loadingMessageText: AppCompatTextView

    private lateinit var connectContainer: LinearLayout
    private lateinit var connectButton: AppCompatButton

    private var btHandler: BtHandler = BtHandler(this)
    private var btController: BtController = BtController(this.btHandler)
    private var palDevice: BluetoothDevice? = null

    private var showErrorMessage: Boolean = false

    override fun getLayoutId(): Int = R.layout.fragment_conversation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.currentdevice = arguments!!.getParcelable(BUNDLE_USER)
        this.pal = arguments!!.getParcelable(BUNDLE_PAL)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.adapter = ConversationAdapter(this.currentdevice, { messages })

        setViewsById(view)
        setViewListeners()

        setPalInfo(this.pal)
    }

    override fun onResume() {
        super.onResume()

        val state = this.btController.getState()
        when (state) {
            BtHelperState.IDLE, BtHelperState.LISTEN -> {
                this.btController.start()
            }
            BtHelperState.CONNECTED -> {
                setLoadingContainerVisibility(false)
            }
            else -> { }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.btController.stop()
    }

    private fun setViewsById(view: View) {
        this.recyclerView = view.fragment_conversation_rv
        this.messageEditText = view.fragment_conversation_message_et
        this.sendButton = view.fragment_conversation_send_btn

        this.palNameText = view.fragment_conversation_pal_name_txt
        this.palAddressText = view.fragment_conversation_pal_address_txt

        this.loadingContainer = view.fragment_conversation_loading_container
        this.loading = view.fragment_conversation_loading
        this.loadingMessageText = view.fragment_conversation_loading_message_txt

        this.connectContainer = view.fragment_conversation_connect_container
        this.connectButton = view.fragment_conversation_connect_btn
    }

    private fun setViewListeners() {
        this.messageEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                AppUtils.setButtonActive(sendButton, AppUtils.stringNotEmpty(s))
            }
        })

        this.messageEditText.clearFocus()

        val layoutManager = LinearLayoutManager(this.context)
        layoutManager.stackFromEnd = true

        this.recyclerView.layoutManager = layoutManager
        this.recyclerView.adapter = this.adapter

        this.sendButton.setOnClickListener { onSendButtonClicked() }
        AppUtils.setButtonActive(sendButton, false)

        this.connectButton.setOnClickListener { onConnectButtonClicked() }
    }

    override fun onMessageStateChanged(state: BtHelperState) {
        if (state == BtHelperState.NO_CONNECTION || state == BtHelperState.CONNECTION_LOST) {
            this.showErrorMessage = true
        }

        when (state) {
            BtHelperState.IDLE -> { }
            BtHelperState.LISTEN -> {
                AppUtils.setVisibility(this.loadingMessageText, true)

                if (!this.showErrorMessage) {
                    setLoadingMessage(R.string.bt_helper_listening)
                }

                this.showErrorMessage = false
            }
            BtHelperState.CONNECTING -> {
                AppUtils.startTransition(this.loadingContainer)

                setLoadingContainerVisibility(true)
                setConnectContainerVisibility(false)

                AppUtils.setVisibility(this.loadingMessageText, true)
                setLoadingMessage(R.string.bt_helper_connecting)

                AppUtils.setVisibility(this.loading, true)
            }
            BtHelperState.CONNECTED -> {
                AppUtils.startTransition(this.loadingContainer)

                setLoadingContainerVisibility(false)
                setConnectContainerVisibility(false)

                AppUtils.setVisibility(this.loadingMessageText, true)
                setLoadingMessage(R.string.bt_helper_connected)

                AppUtils.setVisibility(this.loading, false)
            }
            BtHelperState.CONNECTION_LOST -> {
                AppUtils.startTransition(this.loadingContainer)

                setLoadingContainerVisibility(true)
                setConnectContainerVisibility(true)

                toast(R.string.bt_helper_lost, Toast.LENGTH_LONG)
                AppUtils.setVisibility(this.loading, false)
            }
            BtHelperState.NO_CONNECTION -> {
                AppUtils.startTransition(this.loadingContainer)

                setLoadingContainerVisibility(true)
                setConnectContainerVisibility(true)

                setLoadingMessage(R.string.bt_helper_unable)
                AppUtils.setVisibility(this.loading, false)
            }
        }
    }

    override fun onMessageRead(message: String) {
        addMessage(Message(message, this.pal))
    }

    override fun onMessageDeviceObject(device: BluetoothDevice) {
        if (device.address == this.pal.address) {
            this.palDevice = device

            toast(R.string.bt_helper_connected, Toast.LENGTH_SHORT)
        } else {
            toast(R.string.bt_helper_unable, Toast.LENGTH_SHORT)
            finish()
        }
    }

    private fun onSendButtonClicked() {
        if (this.btController.getState() == BtHelperState.CONNECTED) {
            val message = AppUtils.getEditTextValue(this.messageEditText)

            if (AppUtils.stringNotEmpty(message)) {
                addMessage(createMessage())
                this.messageEditText.setText("")

                this.btController.write(message.toByteArray())
            }
        } else {
            toast(R.string.bt_helper_lost, Toast.LENGTH_LONG)
        }
    }

    private fun onConnectButtonClicked() {
        val btController = this.btController

        if (btController.getState() == BtHelperState.IDLE || btController.getState() == BtHelperState.LISTEN) {
            btController.connect(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(this.pal.address))
        }
    }

    private fun createMessage(): Message {
        return Message(AppUtils.getEditTextValue(this.messageEditText), this.currentdevice)
    }

    private fun addMessage(message: Message) {
        this.adapter.add(message)
        this.recyclerView.smoothScrollToPosition(this.adapter.itemCount - 1)
    }

    private fun setPalInfo(pal: Device) {
        setPalInfo(pal.name, pal.address)
    }

    private fun setPalInfo(userName: String, address: String) {
        this.palNameText.text = if (AppUtils.stringNotEmpty(userName)) userName else "Unknown"
        this.palAddressText.text = address
    }

    private fun setLoadingContainerVisibility(visible: Boolean) {
        AppUtils.setVisibility(this.loadingContainer, visible)
    }

    private fun setConnectContainerVisibility(visible: Boolean) {
        AppUtils.setVisibility(this.connectContainer, visible)
    }

    private fun setLoadingMessage(@StringRes id: Int) {
        this.loadingMessageText.text = this.activity?.getString(id) ?: ""
    }

}