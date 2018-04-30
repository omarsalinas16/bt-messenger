package com.omarsalinas.btmessenger.models

import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.NonNull
import org.jetbrains.annotations.NotNull
import java.text.SimpleDateFormat
import java.util.*

/**
 * Describes a message sent from user to user
 * @param content The string message
 * @param author The creator of the message
 */
data class Message(val content: String, @NonNull @NotNull val author: User) : Parcelable, Comparable<Message> {

    val timestamp: Date = Date()

    val formattedDate: String
        get() {
            return SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US).format(this.timestamp)
        }

    override fun compareTo(other: Message): Int {
        if (this.content == other.content && this.author == other.author && this.timestamp == other.timestamp) {
            return 0
        }

        return 1
    }

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