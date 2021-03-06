package com.ismealdi.dactiv.base

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.MotionEvent
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.ismealdi.dactiv.App
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.activity.MessageActivity
import com.ismealdi.dactiv.activity.NotificationActivity
import com.ismealdi.dactiv.interfaces.AmConnectionInterface
import com.ismealdi.dactiv.util.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_primary.*
import kotlinx.android.synthetic.main.view_loader_state.*


/**
 * Created by Al on 19/10/2018
 */

@SuppressLint("Registered")
open class AmActivity : AppCompatActivity(), AmConnectionInterface {

    private var connectionReceiver : ConnectionReceiver? = null
    private var isRegisteredReceiver: Boolean = false

    protected var db : FirebaseFirestore? = null

    internal fun setTitle(title: String, isPrimary: Boolean = false) {
        if(textTitleToolbar.text != title) {
            textTitleToolbar.setTextFade(title)

            if(isPrimary) {
                textTitleToolbar.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorWhite))
                toolbar.setBackgroundColor(ContextCompat.getColor(applicationContext,R.color.colorPrimary))

            }else{
                textTitleToolbar.setTextColor(ContextCompat.getColor(applicationContext,R.color.colorPrimary))
                toolbar.setBackgroundColor(ContextCompat.getColor(applicationContext,R.color.colorWhite))
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            Utils.Keyboard.auto(event, currentFocus, applicationContext)
        }

        return super.dispatchTouchEvent(event)
    }

    protected fun showSnackBar(message: String, duration: Int = Snackbar.LENGTH_SHORT, actionText: String = "", actionListener: Runnable? = null) {
        val mSnackBar = Snackbar.make(layoutParent, message, duration)

        if(message.contains("Unable to resolve")) mSnackBar.behavior = NoSwipeBehavior()

        mSnackBar.view.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))

        val textViewAction = mSnackBar.view.findViewById(android.support.design.R.id.snackbar_action) as TextView
        val textView = mSnackBar.view.findViewById(android.support.design.R.id.snackbar_text) as TextView

        textView.setTypeface(Typeface.createFromAsset(applicationContext.assets, "fonts/Montserrat-R.ttf"), Typeface.NORMAL)
        textView.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorWhite))
        textView.gravity = Gravity.CENTER
        textView.textSize = 11.5f
        textView.maxLines = 2

        if(actionText != "") {

            textViewAction.setTypeface(Typeface.createFromAsset(applicationContext.assets, "fonts/Montserrat-B.ttf"), Typeface.NORMAL)
            textViewAction.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorWhite))
            textViewAction.gravity = Gravity.CENTER
            textViewAction.background = ContextCompat.getDrawable(applicationContext, R.drawable.button_primary_flat_snackbar)
            textViewAction.textSize = 11.5f

            mSnackBar.setAction(actionText) { actionListener?.run() }
        }

        Handler().postDelayed({
            mSnackBar.show()
        }, (if(actionText != "") 500 else 0).toLong())
    }

    fun initData(receiver: AmConnectionInterface) {

        if(App.fireBaseAuth.currentUser != null) {
            db = App.fireStoreBase

            handleOnNotified()
        }

        if(connectionReceiver == null) {
            connectionReceiver = ConnectionReceiver()
            connectionReceiver!!.registerReceiver(receiver)

            val mIntentFilter = IntentFilter()
            mIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")

            registerReceiver(connectionReceiver, mIntentFilter)
            isRegisteredReceiver = true
        }
    }

    override fun onConnectionChange(message: String) {
        showSnackBar(message)
        Logs.e(message)

        if(textLoader != null) {
            if(message.contains(getString(R.string.text_no_internet)))
                textLoader.setTextFade(getString(R.string.text_offline))
            else
                textLoader.setTextFade("")
        }
    }

    fun String.toNumber(): String {
        return replace(Regex("""[.,Rp ]"""), "")
    }

    override fun onPause() {
        super.onPause()
        if(isRegisteredReceiver) {
            unregisterReceiver(connectionReceiver)
            isRegisteredReceiver = false
        }
    }

    private fun handleOnNotified() {
        val mIntent = Intent(applicationContext, if(intent.getStringExtra(Constants.INTENT.LOGIN.PUSH.MESSAGE) != null && intent.getStringExtra(Constants.INTENT.LOGIN.PUSH.MESSAGE) != "") MessageActivity::class.java else NotificationActivity::class.java)

        if(!intent.getStringExtra(Constants.INTENT.LOGIN.PUSH.NAME).isNullOrEmpty()) {
            mIntent.putExtra(Constants.INTENT.LOGIN.PUSH.SATKER, intent.getStringExtra(Constants.INTENT.LOGIN.PUSH.SATKER))
            mIntent.putExtra(Constants.INTENT.LOGIN.PUSH.MESSAGE, intent.getStringExtra(Constants.INTENT.LOGIN.PUSH.MESSAGE))
            mIntent.putExtra(Constants.INTENT.LOGIN.PUSH.NAME, intent.getStringExtra(Constants.INTENT.LOGIN.PUSH.NAME))
            mIntent.putExtra(Constants.INTENT.LOGIN.PUSH.DESCRIPTION, intent.getStringExtra(Constants.INTENT.LOGIN.PUSH.DESCRIPTION))
            mIntent.putExtra(Constants.INTENT.LOGIN.PUSH.DATE, intent.getStringExtra(Constants.INTENT.LOGIN.PUSH.DATE))
            mIntent.putExtra(Constants.INTENT.LOGIN.PUSH.ID, intent.getStringExtra(Constants.INTENT.LOGIN.PUSH.ID))
            Handler().postDelayed({startActivity(mIntent)}, 250)
        }
    }
}