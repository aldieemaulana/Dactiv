package com.ismealdi.dactiv.util

import android.content.Context
import android.preference.PreferenceManager
import com.ismealdi.dactiv.util.Constants.SHARED.pushToken


/**
 * Created by Al on 20/10/2018
 */
class Preferences(context: Context) {

    private var sharedPref = PreferenceManager.getDefaultSharedPreferences(context)

    fun storeToken(string: String) {
        with (sharedPref.edit()) {
            putString(pushToken, string)
            apply()
        }
    }
    fun getToken() : String {
        return sharedPref.getString(pushToken, "")
    }

}