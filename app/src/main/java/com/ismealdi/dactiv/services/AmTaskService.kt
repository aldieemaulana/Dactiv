package com.ismealdi.dactiv.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.ismealdi.dactiv.util.Logs
import com.ismealdi.dactiv.structure.User.Path.Fields as UserFields
import com.ismealdi.dactiv.structure.User.Path.Name as UserTableName

/**
 * Created by Al on 22/10/2018
 */

class AmTaskService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Logs.d("ClearFromRecentService")
        return START_STICKY_COMPATIBILITY
    }

    override fun onLowMemory() {
        AmMessagingService().storeOnline(false)
        super.onLowMemory()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        AmMessagingService().storeOnline(false)
        stopSelf()
    }


}