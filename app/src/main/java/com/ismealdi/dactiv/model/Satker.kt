package com.ismealdi.dactiv.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

open class Satker(
    var id: String = "",
    var kodeSatker: String = "",
    var name: String = "",
    var description: String = "",
    var kepala: String = "",
    var admin: String = "",
    var eselon: List<String> = emptyList(),
    var createdOn: Timestamp = Timestamp.now(),
    var lastUpdated: Timestamp = Timestamp.now()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readParcelable(Timestamp::class.java.classLoader),
        parcel.readParcelable(Timestamp::class.java.classLoader)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(kodeSatker)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(kepala)
        parcel.writeString(admin)
        parcel.writeStringList(eselon)
        parcel.writeParcelable(createdOn, flags)
        parcel.writeParcelable(lastUpdated, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Satker> {
        override fun createFromParcel(parcel: Parcel): Satker {
            return Satker(parcel)
        }

        override fun newArray(size: Int): Array<Satker?> {
            return arrayOfNulls(size)
        }
    }
}
