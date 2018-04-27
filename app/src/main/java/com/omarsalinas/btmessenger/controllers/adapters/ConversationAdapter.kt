package com.omarsalinas.btmessenger.controllers.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.omarsalinas.btmessenger.R
import com.omarsalinas.btmessenger.models.Message
import com.omarsalinas.btmessenger.models.User
import kotlinx.android.synthetic.main.item_message.view.*

class ConversationAdapter(
        val user: User
) : RecyclerView.Adapter<ConversationAdapter.ConversationHolder>() {

    private var list: ArrayList<Message> = arrayListOf()

    override fun getItemCount(): Int = this.list.size

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

    override fun onBindViewHolder(holder: ConversationHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class ConversationHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private val nameText: TextView = this.view.item_message_name_txt
        private val contentText: TextView = this.view.item_message_content_txt
        private val timestampText: TextView = this.view.item_message_timestamp_txt

        fun bind(message: Message) {
            this.nameText.text = message.author.userName
            this.contentText.text = message.content
            this.timestampText.text = message.formattedDate
        }

    }

}