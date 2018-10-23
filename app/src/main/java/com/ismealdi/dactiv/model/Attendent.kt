package com.ismealdi.dactiv.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

open class Attendent(
        var user: String = "",
        var kegiatan: String = "",
        var attendOn: Timestamp = Timestamp.now(),
        var lastUpdated: Timestamp = Timestamp.now()
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readParcelable(Timestamp::class.java.classLoader),
            parcel.readParcelable(Timestamp::class.java.classLoader)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(user)
        parcel.writeString(kegiatan)
        parcel.writeParcelable(attendOn, flags)
        parcel.writeParcelable(lastUpdated, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Attendent> {
        override fun createFromParcel(parcel: Parcel): Attendent {
            return Attendent(parcel)
        }

        override fun newArray(size: Int): Array<Attendent?> {
            return arrayOfNulls(size)
        }
    }
}
