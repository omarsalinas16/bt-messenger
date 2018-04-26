package com.omarsalinas.btmessenger.models

import android.os.Parcel
import android.os.Parcelable

/**
 * Represents a User with a non-unique [userName] and a Bluetooth MAC address
 * @param userName The username that identifies the user
 * @param btAddress The Bluetooth device address string
 */
data class User(val userName: String, val btAddress: String) : Parcelable {

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(userName)
        writeString(btAddress)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User = User(source)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }
    }

}