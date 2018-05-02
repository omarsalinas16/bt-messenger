package com.omarsalinas.btmessenger.models

import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.Nullable

/**
 * Represents a Device with a non-unique [name] and a Bluetooth MAC address
 * @param name The username that identifies the user
 * @param address The Bluetooth device address string
 */
class Device(@Nullable name: String?, val address: String, val paired: Boolean = false) : Comparable<Device>, Parcelable {
    val name: String = name ?: ""

    override fun equals(other: Any?): Boolean {
        return if (other is Device) {
            compareTo(other) == 0
        } else {
            false
        }
    }

    override fun compareTo(other: Device): Int {
        if (this.name == other.name && this.address == other.address) {
            return 0
        }

        return 1
    }

    override fun hashCode(): Int {
        var result = address.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            1 == source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeString(address)
        writeInt((if (paired) 1 else 0))
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Device> = object : Parcelable.Creator<Device> {
            override fun createFromParcel(source: Parcel): Device = Device(source)
            override fun newArray(size: Int): Array<Device?> = arrayOfNulls(size)
        }
    }

}