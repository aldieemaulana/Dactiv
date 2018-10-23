package com.ismealdi.dactiv.activity

import android.app.NotificationManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.format.DateFormat
import android.view.View
import com.google.firebase.messaging.RemoteMessage
import com.ismealdi.dactiv.App
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.base.AmActivity
import com.ismealdi.dactiv.model.Message
import com.ismealdi.dactiv.model.User
import com.ismealdi.dactiv.util.Constants
import com.ismealdi.dactiv.util.message
import com.ismealdi.dactiv.util.user
import kotlinx.android.synthetic.main.dialog_message.*
import java.util.*


class MessageActivity : AmActivity() {

    private var satkerId = ""
    private var mUser : User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_message)

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        init()
    }

    private fun init() {
        if(intent.getStringExtra(Constants.INTENT.LOGIN.PUSH.DESCRIPTION) != "") {
            bell()
        }

        buttonSend.isEnabled = false

        val remoteMessage = intent.getParcelableExtra<RemoteMessage>(Constants.INTENT.NOTIFICATION)

        if(remoteMessage != null) {
            remoteMessage.data?.isNotEmpty()?.run {
                val data = remoteMessage.data
                textNotification.text = data["title"]
                textDescription.text = data["description"]
                textDate.text = data["date"]

                val userId = remoteMessage.data["fromUser"]
                satkerId = remoteMessage.data["satker"].toString()
                App.fireStoreBase.user(userId.toString()).addSnapshotListener { documentSnapshot, _ ->

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        mUser = documentSnapshot.toObject(User::class.java)
                        if(mUser != null) {
                            buttonSend.isEnabled = true
                            textNotification.setTextColor(ContextCompat.getColor(applicationContext, if(mUser!!.onlineUser) R.color.colorPrimary else R.color.colorTextNormal))
                            viewOnline.visibility = if(mUser!!.onlineUser) View.VISIBLE else View.GONE
                        }
                    }

                }
            }
        }else{
            if(!intent.getStringExtra(Constants.INTENT.LOGIN.PUSH.NAME).isNullOrEmpty()) {
                textNotification.text = intent.getStringExtra(Constants.INTENT.LOGIN.PUSH.NAME)

                if(intent.getStringExtra(Constants.INTENT.LOGIN.PUSH.DESCRIPTION) != "") {
                    textDescription.text = intent.getStringExtra(Constants.INTENT.LOGIN.PUSH.DESCRIPTION)
                }else{
                    textDescription.visibility = View.GONE
                    textNotification.text = "To: " + textNotification.text.toString()
                }

                textDate.text = intent.getStringExtra(Constants.INTENT.LOGIN.PUSH.DATE)

                val userId = intent.getStringExtra(Constants.INTENT.LOGIN.PUSH.MESSAGE)
                satkerId = intent.getStringExtra(Constants.INTENT.LOGIN.PUSH.SATKER)
                App.fireStoreBase.user(userId.toString()).addSnapshotListener { documentSnapshot, _ ->

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        mUser = documentSnapshot.toObject(User::class.java)
                        if(mUser != null) {
                            buttonSend.isEnabled = true
                            textNotification.setTextColor(ContextCompat.getColor(applicationContext, if(mUser!!.onlineUser) R.color.colorPrimary else R.color.colorTextNormal))
                            viewOnline.visibility = if(mUser!!.onlineUser) View.VISIBLE else View.GONE
                        }

                    }

                }
            }
        }

        buttonSend.setOnClickListener {
            val reply = textName.text.toString()

            if(reply.isNotEmpty()) {
                mUser?.let { it1 -> message(it1, satkerId, reply) }

                finish()
                overridePendingTransition(0, R.anim.anim_out)
            }
        }


        val nMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nMgr.cancelAll()
    }

    private fun message(user: User, satker: String, reply: String) {
        val mMessage = Message()

        mMessage.fromUser = App.fireBaseAuth.currentUser!!.uid
        mMessage.toUser = user.uid
        mMessage.toToken = user.pushId
        mMessage.description = reply
        mMessage.title = App.fireBaseAuth.currentUser!!.displayName.toString()
        mMessage.date = DateFormat.format("d MMMM yyyy h:m", Calendar.getInstance()).toString()
        mMessage.satker = satker

        val document = App.fireStoreBase.message().document()
        document.set(mMessage).addOnFailureListener {
            showSnackBar(it.message.toString())
        }


    }

    private fun bell() {
        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()

        finish()
        overridePendingTransition(0, R.anim.anim_out)
    }

}
