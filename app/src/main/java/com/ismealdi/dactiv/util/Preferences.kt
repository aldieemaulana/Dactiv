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
}