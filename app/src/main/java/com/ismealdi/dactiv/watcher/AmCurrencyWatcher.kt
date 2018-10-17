package com.ismealdi.dactiv.watcher

import android.text.Editable
import android.text.TextWatcher
import com.ismealdi.dactiv.components.AmEditText
import java.util.*


/**
 * Created by Al on 18/10/2018
 */

class AmCurrencyWatcher(private val et: AmEditText) : TextWatcher {

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun afterTextChanged(s: Editable) {
        val cursorPosition = et.selectionEnd
        val originalStr = et.text.toString()

        try {
            et.removeTextChangedListener(this)
            val value = et.text.toString()
            if (value != "")
            {
                if (value.startsWith("."))
                {
                    et.setText("0.")
                }
                if (value.startsWith("0") && !value.startsWith("0."))
                {
                    et.setText("")
                }
                val str = et.text.toString().replace(",", "")
                if (value != "")
                    et.setText(getDecimalFormattedString(str))
                val diff = et.text.toString().length - originalStr.length
                et.setSelection(cursorPosition + diff)
            }
            et.addTextChangedListener(this)
        } catch (ex:Exception) {
            ex.printStackTrace()
            et.addTextChangedListener(this)
        }
    }

    private fun getDecimalFormattedString(value: String?): String {
        if (value != null && !value.equals("", ignoreCase = true)) {
            val lst = StringTokenizer(value, ".")
            var str1: String = value
            var str2 = ""
            if (lst.countTokens() > 1) {
                str1 = lst.nextToken()
                str2 = lst.nextToken()
            }
            var str3 = ""
            var i = 0
            var j = -1 + str1.length
            if (str1[-1 + str1.length] == '.') {
                j--
                str3 = "."
            }
            var k = j
            while (true) {
                if (k < 0) {
                    if (str2.length > 0)
                        str3 = "$str3.$str2"
                    return str3
                }
                if (i == 3) {
                    str3 = ",$str3"
                    i = 0
                }
                str3 = str1[k] + str3
                i++
                k--
            }
        }
        return ""
    }

}