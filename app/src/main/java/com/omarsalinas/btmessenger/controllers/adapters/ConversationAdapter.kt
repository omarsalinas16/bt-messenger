package com.omarsalinas.btmessenger.controllers.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.omarsalinas.btmessenger.R
import com.omarsalinas.btmessenger.common.AppUtils
import com.omarsalinas.btmessenger.common.SimpleAdapter
import com.omarsalinas.btmessenger.common.SimpleHolder
import com.omarsalinas.btmessenger.models.Message
import com.omarsalinas.btmessenger.models.User
import kotlinx.android.synthetic.main.item_message.view.*
import org.joda.time.DateTime
import org.joda.time.Minutes

class ConversationAdapter(
        private val user: User
) : SimpleAdapter<Message, ConversationAdapter.ConversationHolder>() {

    companion object {
        private val MINUTES_TO_JOIN: Minutes = Minutes.ONE
    }

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
        val current = this.list[position]
        var join = false

        if (position > 0 && position < this.list.size) {
            val previous = this.list[position - 1]

            if (current.author == previous.author) {
                val minutes = Minutes.minutesBetween(DateTime(previous.timestamp), DateTime(current.timestamp))
                join = minutes.isLessThan(MINUTES_TO_JOIN)
            }
        }

        holder.bind(current, join)
    }

    override fun add(item: Message): Boolean {
        this.list.add(item)
        notifyItemInserted(this.list.size - 1)

        return true
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

        fun bind(item: Message, join: Boolean = false) {
            bind(item)

            AppUtils.setVisibility(this.nameText, !join)
            AppUtils.setVisibility(this.timestampText, !join)
        }

    }

}