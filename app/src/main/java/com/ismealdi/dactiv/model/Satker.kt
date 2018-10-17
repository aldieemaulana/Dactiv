package com.ismealdi.dactiv.model

import com.google.firebase.Timestamp

open class Satker(
    var id: String = "",
    var name: String = "",
    var description: String = "",
    var kepala: String = "",
    var admin: String = "",
    var eselon: List<String> = emptyList(),
    var createdOn: Timestamp = Timestamp.now(),
    var lastUpdated: Timestamp = Timestamp.now()
)
