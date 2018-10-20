package com.ismealdi.dactiv.util

import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

object Utils {

    object Keyboard {
        fun show(context: Context) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

        fun hide(context: Context, view: View) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun auto(event: MotionEvent, currentFocus: View?, context: Context) {
            if(currentFocus != null) {
                if (currentFocus is EditText) {
                    val outRect = Rect()
                    currentFocus.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        currentFocus.clearFocus()
                        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
                    }
                }
            }
        }
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
