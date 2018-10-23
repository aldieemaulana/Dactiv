package com.ismealdi.dactiv.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import java.util.*

open class Kegiatan(
        var id: String = "",
        var name: String = "",
        var satker: String = "",
        var kodeKegiatan: String = "",
        var anggaran: Long = 0,
        var jadwal: Date = Date(),
        var durasi: Int = 0,
        var penanggungJawab: String = "",
        var bagian: String = "",
        var status: Int = 0,
        var admin: String = "",
        var attendent: List<Attendent> = emptyList(),
        var createdOn: Timestamp = Timestamp.now(),
        var lastUpdated: Timestamp = Timestamp.now()
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readDate()!!,
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.createTypedArrayList(Attendent),
            parcel.readParcelable(Timestamp::class.java.classLoader),
            parcel.readParcelable(Timestamp::class.java.classLoader)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(satker)
        parcel.writeString(kodeKegiatan)
        parcel.writeLong(anggaran)
        parcel.writeDate(jadwal)
        parcel.writeInt(durasi)
        parcel.writeString(penanggungJawab)
        parcel.writeString(bagian)
        parcel.writeInt(status)
        parcel.writeString(admin)
        parcel.writeTypedList(attendent)
        parcel.writeParcelable(createdOn, flags)
        parcel.writeParcelable(lastUpdated, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Kegiatan> {
        override fun createFromParcel(parcel: Parcel): Kegiatan {
            return Kegiatan(parcel)
        }

        override fun newArray(size: Int): Array<Kegiatan?> {
            return arrayOfNulls(size)
        }
    }
}

fun Parcel.writeDate(date: Date?) {
    writeLong(date?.time ?: -1)
}

fun Parcel.readDate(): Date? {
    val long = readLong()
    return if (long != -1L) Date(long) else null
}