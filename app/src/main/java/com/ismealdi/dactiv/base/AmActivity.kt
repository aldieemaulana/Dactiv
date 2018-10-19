package com.ismealdi.dactiv.base

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.TextView
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.util.NoSwipeBehavior
import kotlinx.android.synthetic.main.activity_sign_in.*


/**
 * Created by Al on 19/10/2018
 */

@SuppressLint("Registered")
open class AmActivity : AppCompatActivity() {

    protected fun showSnackBar(message: String, duration: Int = Snackbar.LENGTH_SHORT, actionText: String = "", actionListener: Runnable? = null) {
        val mSnackBar = Snackbar.make(layoutParent, message, duration)

        if(message.contains("Unable to resolve")) mSnackBar.behavior = NoSwipeBehavior()

        mSnackBar.view.setBackgroundColor(applicationContext.resources.getColor(R.color.colorPrimary))

        val textViewAction = mSnackBar.view.findViewById(android.support.design.R.id.snackbar_action) as TextView
        val textView = mSnackBar.view.findViewById(android.support.design.R.id.snackbar_text) as TextView

        textView.setTypeface(Typeface.createFromAsset(applicationContext.assets, "fonts/Montserrat-R.ttf"), Typeface.NORMAL)
        textView.setTextColor(applicationContext.resources.getColor(R.color.colorWhite))
        textView.gravity = Gravity.CENTER
        textView.textSize = 11.5f
        textView.maxLines = 2

        if(actionText != "") {

            textViewAction.setTypeface(Typeface.createFromAsset(applicationContext.assets, "fonts/Montserrat-B.ttf"), Typeface.NORMAL)
            textViewAction.setTextColor(applicationContext.resources.getColor(R.color.colorWhite))
            textViewAction.gravity = Gravity.CENTER
            textViewAction.setBackgroundDrawable(applicationContext.resources.getDrawable(R.drawable.button_primary_flat_snackbar))
            textViewAction.textSize = 11.5f

            mSnackBar.setAction(actionText) { actionListener?.run() }
        }

        Handler().postDelayed({
            mSnackBar.show()
        }, (if(actionText != "") 500 else 0).toLong())
    }
}