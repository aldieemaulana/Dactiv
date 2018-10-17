package com.ismealdi.dactiv

import android.annotation.SuppressLint
import android.content.Context
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage

/**
 * Created by Al on 10/10/2018
 */

@SuppressLint("StaticFieldLeak")
class App : MultiDexApplication() {

    companion object {
        lateinit var fireBaseAuth : FirebaseAuth
        lateinit var fireStoreBase : FirebaseFirestore
        lateinit var fireStorage: FirebaseStorage
        lateinit var fireBaseMsg: FirebaseMessaging

    }

    override fun onCreate() {
        super.onCreate()
        fireBaseAuth = FirebaseAuth.getInstance()
        fireStoreBase = FirebaseFirestore.getInstance()
        fireStorage = FirebaseStorage.getInstance()
        fireBaseMsg = FirebaseMessaging.getInstance()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}