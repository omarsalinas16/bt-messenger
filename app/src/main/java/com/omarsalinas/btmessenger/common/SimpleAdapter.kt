package com.omarsalinas.btmessenger.common

import android.support.v7.widget.RecyclerView
import android.view.View
import java.util.ArrayList

abstract class SimpleAdapter<T, V: SimpleHolder<T>> : RecyclerView.Adapter<V>() {

    protected val list: ArrayList<T> = arrayListOf()

    override fun getItemCount(): Int = this.list.size

    override fun onBindViewHolder(holder: V, position: Int) {
        holder.bind(this.list[position])
    }

    fun add(item: T): Boolean {
        return if (!this.list.contains(item)) {
            this.list.add(item)
            notifyItemInserted(this.list.size - 1)

            true
        } else {
            false
        }
    }

    fun clear() {
        val size = this.list.size
        this.list.clear()
        notifyItemRangeRemoved(0, size)
    }

}

abstract class SimpleHolder<T>(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun bind(item: T)

}
