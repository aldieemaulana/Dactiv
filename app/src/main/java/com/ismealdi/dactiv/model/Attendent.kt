package com.ismealdi.dactiv.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Attendent(
        var user: String = "",
        var kegiatan: String = "",
        var attendanceNumber: Int = 1,
        var attendOn: Timestamp = Timestamp.now(),
        var lastUpdated: Timestamp = Timestamp.now()
) : Parcelable
