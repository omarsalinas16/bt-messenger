package com.omarsalinas.btmessenger.controllers.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.omarsalinas.btmessenger.R
import com.omarsalinas.btmessenger.common.SimpleAdapter
import com.omarsalinas.btmessenger.common.SimpleHolder
import com.omarsalinas.btmessenger.models.Message
import com.omarsalinas.btmessenger.models.User
import kotlinx.android.synthetic.main.item_message.view.*

class ConversationAdapter(
        private val user: User
) : SimpleAdapter<Message, ConversationAdapter.ConversationHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (user.btAddress == list[position].author.btAddress)
            R.layout.item_message
        else
            R.layout.item_message_incoming
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ConversationHolder(view)
    }

    inner class ConversationHolder(view: View) : SimpleHolder<Message>(view) {

        private val nameText: TextView = view.item_message_name_txt
        private val contentText: TextView = view.item_message_content_txt
        private val timestampText: TextView = view.item_message_timestamp_txt

        override fun bind(item: Message) {
            this.nameText.text = item.author.userName
            this.contentText.text = item.content
            this.timestampText.text = item.formattedDate
        }

    }

}