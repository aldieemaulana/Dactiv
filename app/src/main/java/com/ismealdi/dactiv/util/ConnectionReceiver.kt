package com.ismealdi.dactiv.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.ismealdi.dactiv.interfaces.AmConnectionInterface
import com.ismealdi.dactiv.services.AmMessagingService

/**
 * Created by Al on 14/10/2018
 */

class ConnectionReceiver : BroadcastReceiver() {

    private var callback: AmConnectionInterface? = null
    private var isSwitching = false

    override fun onReceive(context: Context, arg1: Intent) {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        Logs.e("Internet Check")
        val isConnected = wifi != null && wifi.isConnectedOrConnecting || mobile != null && mobile.isConnectedOrConnecting
        if (!isConnected) {
            if(!isSwitching) {
                AmMessagingService().storeOnline(false)
                showMessage("No Internet Connection")
                isSwitching = true
            }
        }else{
            if(isSwitching) {
                AmMessagingService().storeOnline(true)
                showMessage("Internet Connected")
                isSwitching = false
            }
        }
    }

    private fun showMessage(message: String) {
        if(this.callback != null) {
            this.callback!!.onConnectionChange(message)
            Logs.e(message)
        } else {
            Logs.e("No Callback for Internet State")
        }
    }

    fun registerReceiver(receiver: AmConnectionInterface) {
        this.callback = receiver
    }

}