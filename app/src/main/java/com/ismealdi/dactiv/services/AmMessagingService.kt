package com.ismealdi.dactiv.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.google.firebase.Timestamp
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ismealdi.dactiv.App
import com.ismealdi.dactiv.activity.NotificationActivity
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.activity.MainActivity
import com.ismealdi.dactiv.util.Constants
import com.ismealdi.dactiv.util.Logs
import com.ismealdi.dactiv.structure.User.Path.Fields as UserFields
import com.ismealdi.dactiv.structure.User.Path.Name as UserTableName


/**
 * Created by Al on 16/10/2018
 */

class AmMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Logs.d("From: ${remoteMessage?.from}")

        // message body = remoteMessage.notification.body
        remoteMessage?.data?.isNotEmpty()?.run {


            Logs.d("Message data payload: " + remoteMessage.data)
        }

        val intent = Intent(this, NotificationActivity::class.java)
        intent.putExtra(Constants.INTENT.NOTIFICATION, remoteMessage)
        startActivity(intent)

    }

    override fun onNewToken(token: String?) {
        Logs.d("Refreshed token: " + token!!)
        sendRegistrationToServer(token)
    }

    fun sendRegistrationToServer(token: String) {
        if(!token.isEmpty()){
            val data : MutableMap<String, Any> = mutableMapOf()

            data[UserFields.pushId] = token
            data[UserFields.lastUpdated] = Timestamp.now()

            if(App.fireBaseAuth.currentUser != null) {
                App.fireStoreBase.collection(UserTableName).document(App.fireBaseAuth.currentUser!!.uid).update(data.toMap())
                    .addOnFailureListener {
                        Logs.d("Failed update token: " + it.message!!)
                    }
            }
        }
    }

    private fun sendNotification(messageBody: String, response: RemoteMessage) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(Constants.INTENT.NOTIFICATION, response)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString(R.string.default_channel)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.default_channel))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }


}