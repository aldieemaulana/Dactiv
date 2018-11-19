package com.ismealdi.dactiv.util

import android.content.Context
import android.preference.PreferenceManager

/**
 * Created by Al on 20/10/2018
 */
class Preferences(context: Context) {

    private var sharedPref = PreferenceManager.getDefaultSharedPreferences(context)

    fun storeToken(string: String) {
        with (sharedPref.edit()) {
            putString(Constants.SHARED.pushToken, string)
            apply()
        }
    }

    fun getToken() : String {
        return sharedPref.getString(Constants.SHARED.pushToken, "")!!
    }

    fun storeUid(string: String) {
        with (sharedPref.edit()) {
            putString(Constants.SHARED.userUid, string)
            apply()
        }
    }

    fun getUid() : String {
        return sharedPref.getString(Constants.SHARED.userUid, "")!!
    }

    fun storeFirstLoadRapat(boolean: Boolean) {
        with (sharedPref.edit()) {
            putBoolean(Constants.SHARED.firstLoadRapat, boolean)
            apply()
        }
    }

    fun getFirstLoadRapat() : Boolean {
        return sharedPref.getBoolean(Constants.SHARED.firstLoadRapat, false)
    }

    fun storeFirstLoadKegiatan(boolean: Boolean) {
        with (sharedPref.edit()) {
            putBoolean(Constants.SHARED.firstLoadKegiatan, boolean)
            apply()
        }
    }

    fun getFirstLoadKegiatan() : Boolean {
        return sharedPref.getBoolean(Constants.SHARED.firstLoadKegiatan, false)
    }

    fun storeFirstLoadSatker(boolean: Boolean) {
        with (sharedPref.edit()) {
            putBoolean(Constants.SHARED.firstLoadSatker, boolean)
            apply()
        }
    }

    fun getFirstLoadSatker() : Boolean {
        return sharedPref.getBoolean(Constants.SHARED.firstLoadSatker, false)
    }

}