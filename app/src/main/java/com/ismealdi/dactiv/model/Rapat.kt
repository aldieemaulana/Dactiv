package com.ismealdi.dactiv.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Rapat(
    var id: String = "",
    var agendaRapat: String = "",
    var notulenRapat: String = "",
    var tindakLanjut: Int = 0,
    var tanggalBatasTindakLanjut: Date = Date(),
    var tanggalRapat: Date = Date(),
    var status: Int = 0,
    var admin: String = "",
    var attendent: List<Attendent> = emptyList(),
    var createdOn: Date = Timestamp.now().toDate(),
    var lastUpdated: Date = Timestamp.now().toDate()
) : Parcelable
