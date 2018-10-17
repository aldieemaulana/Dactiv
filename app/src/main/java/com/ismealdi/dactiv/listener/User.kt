package com.ismealdi.dactiv.listener

import android.content.Context
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.ismealdi.dactiv.App
import com.ismealdi.dactiv.base.AmActivity
import com.ismealdi.dactiv.model.User
import com.ismealdi.dactiv.util.Logs


/**
 * Created by Al on 15/10/2018
 */

object User {

    object Path {
        const val Name = "users"

        object Fields {
            const val uid = "uid"
            const val providerId = "providerId"
            const val displayName = "displayName"
            const val photoUrl = "photoUrl"
            const val email = "email"
            const val phoneNumber = "phoneNumber"
            const val emailVerified = "emailVerified"
            const val category = "category"
            const val golongan = "golongan"
            const val bagian = "bagian"
            const val nip = "nip"
            const val pushId = "pushId"
            const val lastUpdated = "lastUpdated"
        }
    }


    fun addFromRegister(currentUser: FirebaseUser?) {
        val user = User()

        user.uid = currentUser?.uid!!
        user.email = currentUser.email!!
        user.providerId = currentUser.providerId
        user.emailVerified = currentUser.isEmailVerified
        user.bagian = 0
        user.golongan = 0

        App.fireStoreBase.collection(Path.Name).document(user.uid).set(user).addOnSuccessListener {
            Logs.db("Saved user with name, ${user.uid}")
        }.addOnFailureListener {
            Logs.db("Failed user to save, ${it.message}")
        }
    }

    fun verifiedUser(uid: String, verified: Boolean = false) {
        val data : MutableMap<String, Any> = mutableMapOf()

        data[Path.Fields.emailVerified] = verified
        data[Path.Fields.lastUpdated] = Timestamp.now()

        App.fireStoreBase.collection(Path.Name).document(uid).update(data.toMap())
        .addOnCompleteListener {
            Logs.db("Updated user with name, $uid")
        }.addOnFailureListener { Logs.db("Failed user to updated, ${it.message}") }
    }

}