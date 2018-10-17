package com.ismealdi.dactiv.watcher

import android.text.Editable
import android.text.TextWatcher


/**
 * Created by Al on 18/10/2018
 */

class AmFourDigitWatcher : TextWatcher {

    private val TOTAL_SYMBOLS = 14
    private val TOTAL_DIGITS = 12
    private val DIVIDER_MODULO = 5
    private val DIVIDER_POSITION = DIVIDER_MODULO - 1
    private val DIVIDER = '.'

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun afterTextChanged(s: Editable) {
        if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
            s.replace(0, s.length, buildCorrectString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER))
        }
    }

    private fun isInputCorrect(s: Editable, totalSymbols: Int, dividerModulo: Int, divider: Char): Boolean {
        var isCorrect = s.length <= totalSymbols // check size of entered string
        for (i in 0 until s.length) { // check that every element is right
            isCorrect = if (i > 0 && (i + 1) % dividerModulo == 0) {
                isCorrect and (divider == s[i])
            } else {
                isCorrect and Character.isDigit(s[i])
            }
        }
        return isCorrect
    }

    private fun buildCorrectString(digits: CharArray, dividerPosition: Int, divider: Char): String {
        val formatted = StringBuilder()

        for (i in digits.indices) {
            if (digits[i].toInt() != 0) {
                formatted.append(digits[i])
                if (i > 0 && i < digits.size - 1 && (i + 1) % dividerPosition == 0) {
                    formatted.append(divider)
                }
            }
        }

        return formatted.toString()
    }

    private fun getDigitArray(s: Editable, size: Int): CharArray {
        val digits = CharArray(size)
        var index = 0
        var i = 0
        while (i < s.length && index < size) {
            val current = s[i]
            if (Character.isDigit(current)) {
                digits[index] = current
                index++
            }
            i++
        }
        return digits
    }
}