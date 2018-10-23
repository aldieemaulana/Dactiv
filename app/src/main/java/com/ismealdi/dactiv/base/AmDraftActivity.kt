package com.ismealdi.dactiv.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Handler
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.ismealdi.dactiv.App
import com.ismealdi.dactiv.activity.NotificationActivity
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.activity.MessageActivity
import com.ismealdi.dactiv.interfaces.AmConnectionInterface
import com.ismealdi.dactiv.util.*
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_primary.*

import com.ismealdi.dactiv.structure.Golongan.Path.Fields as GolonganFields
import com.ismealdi.dactiv.structure.Jabatan.Path.Fields as JabatanFields
import com.ismealdi.dactiv.structure.User.Path.Fields as UserFields
import com.ismealdi.dactiv.structure.Satker.Path.Fields as SatkerFields
import com.ismealdi.dactiv.structure.Kegiatan.Path.Fields as KegiatanFields
import com.ismealdi.dactiv.structure.Rapat.Path.Fields as RapatFields

/**
 * Created by Al on 10/10/2018
 */

@SuppressLint("Registered")
open class AmDraftActivity : AppCompatActivity(), AmConnectionInterface {

    companion object {
        val golonganFields = GolonganFields
        val jabatanFields = JabatanFields
        val userFields = UserFields
        val satkerFields = SatkerFields
        val kegiatanFields = KegiatanFields
        val rapatFields = RapatFields
    }

    open var user : FirebaseUser? = null

    protected val context : Context = this
    protected var db : FirebaseFirestore? = null
    protected var auth : FirebaseAuth? = null
    protected var msg : FirebaseMessaging? = null

    var storage : FirebaseStorage? = null

    private var progress: KProgressHUD? = null
    private var connectionReceiver : ConnectionReceiver? = null

    internal fun showProgress() {
        if(progress != null)
            progress!!.show()
    }

    internal fun hideProgress() {
        if(progress != null && progress!!.isShowing)
            progress!!.dismiss()
    }

    internal fun initData(amConnectionInterface: AmConnectionInterface? = null) {
        progress = Dialogs.initProgressDialog(context)

        auth = App.fireBaseAuth
        msg = App.fireBaseMsg

        if(App.fireBaseAuth.currentUser != null) {
            db = App.fireStoreBase
            user = App.fireBaseAuth.currentUser
            storage = App.fireStorage

            handleOnNotified()
        }

        if(amConnectionInterface != null) {
            connectionReceiver = ConnectionReceiver()
            connectionReceiver!!.registerReceiver(amConnectionInterface)

            val mIntentFilter = IntentFilter()
            mIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")

            registerReceiver(connectionReceiver, mIntentFilter)
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            if(currentFocus != null) {
                val v = currentFocus
                if (v is EditText) {
                    val outRect = Rect()
                    v.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        v.clearFocus()
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(v.windowToken, 0)
                    }
                }
            }
        }

        return super.dispatchTouchEvent(event)
    }

    internal fun setTitle(title: String, isPrimary: Boolean = false) {
        if(textTitleToolbar.text != title) {
            textTitleToolbar.setTextFade(title)

            if(isPrimary) {
                textTitleToolbar.setTextColor(context.resources.getColor(R.color.colorWhite))
                toolbar.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))

            }else{
                textTitleToolbar.setTextColor(context.resources.getColor(R.color.colorPrimary))
                toolbar.setBackgroundColor(context.resources.getColor(R.color.colorWhite))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if(connectionReceiver != null)
            unregisterReceiver(connectionReceiver)
    }

    internal fun showSnackBar(view: CoordinatorLayout, message: String, duration: Int = Snackbar.LENGTH_SHORT, delay: Long = 0, actionText: String = "", actionListener: Runnable? = null) {
        val mSnackBar = Snackbar.make(view, message, duration)

        if(message.contains("Unable to resolve")) mSnackBar.behavior = NoSwipeBehavior()

        mSnackBar.view.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))

        val textViewAction = mSnackBar.view.findViewById(android.support.design.R.id.snackbar_action) as TextView
        val textView = mSnackBar.view.findViewById(android.support.design.R.id.snackbar_text) as TextView

        textView.setTypeface(Typeface.createFromAsset(applicationContext.assets, "fonts/Montserrat-R.ttf"), Typeface.NORMAL)
        textView.setTextColor(context.resources.getColor(R.color.colorWhite))
        textView.gravity = Gravity.CENTER
        textView.textSize = 11.5f
        textView.maxLines = 2

        if(actionText != "") {

            textViewAction.setTypeface(Typeface.createFromAsset(applicationContext.assets, "fonts/Montserrat-B.ttf"), Typeface.NORMAL)
            textViewAction.setTextColor(context.resources.getColor(R.color.colorWhite))
            textViewAction.gravity = Gravity.CENTER
            textViewAction.setBackgroundDrawable(context.resources.getDrawable(R.drawable.button_primary_flat_snackbar))
            textViewAction.textSize = 11.5f

            mSnackBar.setAction(actionText) { actionListener?.run() }
        }

        Handler().postDelayed({
            hideProgress()
            mSnackBar.show()
        }, delay)
    }

    override fun onConnectionChange(message: String) {
        showSnackBar(layoutParent, message, Snackbar.LENGTH_SHORT, 850)
        Logs.e(message)
    }

    private fun handleOnNotified() {
        val mIntent = Intent(applicationContext, if(intent.getStringExtra(Constants.INTENT.LOGIN.PUSH.MESSAGE) != "") MessageActivity::class.java else NotificationActivity::class.java)

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

    fun String.toNumber(): String {
        return replace(Regex("""[.,Rp ]"""), "")
    }
}
