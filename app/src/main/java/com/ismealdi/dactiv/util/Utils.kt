package com.ismealdi.dactiv.util

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

object Utils {

    fun showKeyboard(context: Context) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun hideKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus!!.windowToken, 0)
    }

    fun stringKodeFormat(string: String): String {
        val fmt : DecimalFormat
        val symbols = DecimalFormatSymbols()
        symbols.groupingSeparator = '.'

        fmt = when {
            string.length == 12 -> DecimalFormat("0000,0000,0000", symbols)
            string.length == 6 -> DecimalFormat("0000,0000", symbols)
            else -> return string
        }

        return fmt.format(string.toLong()).toString()
    }

}
