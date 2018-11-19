package com.ismealdi.dactiv.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class Satker(
    var id: String = "",
    var kodeSatker: String = "",
    var name: String = "",
    var description: String = "",
    var kepala: String = "",
    var admin: String = "",
    var eselon: List<String> = emptyList(),
    var createdOn: Timestamp = Timestamp.now(),
    var lastUpdated: Timestamp = Timestamp.now()
) : Parcelable
