package com.ismealdi.dactiv

import android.media.RingtoneManager
import android.os.Bundle
import com.google.firebase.messaging.RemoteMessage
import com.ismealdi.dactiv.base.AmActivity
import com.ismealdi.dactiv.util.Constants
import kotlinx.android.synthetic.main.dialog_notification.*


class NotificationDialog : AmActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_notification)

        init()
    }

    private fun init() {
        bell()
        val remoteMessage = intent.getParcelableExtra<RemoteMessage>(Constants.INTENT.NOTIFICATION)
        if(remoteMessage != null) {
            remoteMessage.data?.isNotEmpty()?.run {
                val data = remoteMessage.data
                textNotification.text = data["title"]
                textDescription.text = data["description"]
                textDate.text = data["date"]
            }
        }else{
            if(!intent.getStringExtra(Constants.INTENT.LOGIN.PUSH.NAME).isNullOrEmpty()) {
                textNotification.text = intent.getStringExtra(Constants.INTENT.LOGIN.PUSH.NAME)
                textDescription.text = intent.getStringExtra(Constants.INTENT.LOGIN.PUSH.DESCRIPTION)
                textDate.text = intent.getStringExtra(Constants.INTENT.LOGIN.PUSH.DATE)
            }
        }

        buttonCancel.setOnClickListener {
            finish()
            overridePendingTransition(0, R.anim.anim_out)
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
