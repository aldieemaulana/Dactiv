package com.ismealdi.dactiv.model

import com.google.firebase.Timestamp
import java.util.*

open class Rapat(
    var id: String = "",
    var agendaRapat: String = "",
    var pesertaRapat: List<String> = emptyList(),
    var notulenRapat: String = "",
    var tindakLanjut: Int = 0,
    var tanggalBatasTindakLanjut: Date = Date(),
    var tanggalRapat: Date = Date(),
    var createdOn: Timestamp = Timestamp.now(),
    var lastUpdated: Timestamp = Timestamp.now()
)
