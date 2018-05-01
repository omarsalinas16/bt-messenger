package com.omarsalinas.btmessenger.models

import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.Nullable

/**
 * Represents a User with a non-unique [userName] and a Bluetooth MAC address
 * @param userName The username that identifies the user
 * @param address The Bluetooth device address string
 */
class User(@Nullable userName: String?, val address: String) : Comparable<User>, Parcelable {

    val userName: String = userName ?: "Unknown"

    override fun compareTo(other: User): Int {
        if (this.userName == other.userName && this.address == other.address) {
            return 0
        }

        return 1
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(userName)
        writeString(address)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User = User(source)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }
    }

}