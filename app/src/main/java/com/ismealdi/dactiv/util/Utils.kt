package com.ismealdi.dactiv.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import java.io.ByteArrayOutputStream
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import java.util.concurrent.TimeUnit

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

    fun compressBitmap(bitmap:Bitmap, quality:Int):Bitmap{
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)

        val byteArray = stream.toByteArray()
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    fun getImageUri(context: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, inImage, "DactivFoto", null)
        return Uri.parse(path)
    }

    fun numberOfDays(end: Long) : Long {
        val start = Calendar.getInstance().timeInMillis
        val duration = end - start

        return TimeUnit.MILLISECONDS.toDays(duration)
    }

    fun getImageQrCode(imageView: ImageView, text: String, width: Int = 1000, height: Int = 1000) {
        val multiFormatWriter = MultiFormatWriter()

        try {
            val bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, width, height)
            val barcodeEncoder = BarcodeEncoder()
            imageView.setImageBitmap(barcodeEncoder.createBitmap(bitMatrix))
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

}
