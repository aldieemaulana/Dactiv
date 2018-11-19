package com.ismealdi.dactiv.model

import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName


/**
 * Created by Al on 15/10/2018
 */

open class User(
    var uid: String = "",
    var providerId: String = "",
    var displayName: String = "",
    var photoUrl: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var emailVerified: Boolean = false,
    var category: Int = 2,
    var golongan: Int = 0,
    var bagian: Int = 0,
    var nip: String = "",
    var pushId: String = "",
    var onlineUser: Boolean = false,
    var lastUpdated: Timestamp = Timestamp.now()
)