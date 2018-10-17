package com.ismealdi.dactiv.model

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
    var createdOn: Timestamp = Timestamp.now(),
    var lastUpdated: Timestamp = Timestamp.now()
)
