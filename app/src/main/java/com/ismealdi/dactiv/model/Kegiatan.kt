package com.ismealdi.dactiv.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Kegiatan(
        var id: String = "",
        var name: String = "",
        var satker: String = "",
        var kodeKegiatan: String = "",
        var anggaran: Long = 0,
        var realisasi: Long = 0,
        var alasan: String = "",
        var jadwal: Date = Date(),
        var pelaksanaan: Date = Date(),
        var durasi: Int = 0,
        var penanggungJawab: String = "",
        var bagian: String = "",
        var status: Int = 0,
        var admin: String = "",
        var attendent: List<Attendent> = emptyList(),
        var createdOn: Timestamp = Timestamp.now(),
        var lastUpdated: Timestamp = Timestamp.now()
) : Parcelable