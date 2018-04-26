package com.omarsalinas.btmessenger.models

import android.os.Parcel
import android.os.Parcelable
import java.time.LocalDateTime
import java.util.*

/**
 * Describes a message sent from user to user
 * @param content The string message
 * @param timestamp The moment in time the message was created
 */
data class Message(val content: String, val author: User) : Parcelable {

    val timestamp: Date = Date()

    constructor(source: Parcel) : this(
            source.readString(),
            source.readParcelable<User>(User::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(content)
        writeParcelable(author, 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Message> = object : Parcelable.Creator<Message> {
            override fun createFromParcel(source: Parcel): Message = Message(source)
            override fun newArray(size: Int): Array<Message?> = arrayOfNulls(size)
        }
    }

}