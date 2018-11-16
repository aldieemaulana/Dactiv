package com.ismealdi.dactiv.activity

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.view.View
import com.ismealdi.dactiv.App
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.activity.signin.SignInActivity
import com.ismealdi.dactiv.base.AmDraftActivity
import com.ismealdi.dactiv.services.AmMessagingService
import com.ismealdi.dactiv.services.AmTaskService
import com.ismealdi.dactiv.util.Constants
import com.ismealdi.dactiv.util.Preferences
import com.ismealdi.dactiv.util.RevealAnimation
import kotlinx.android.synthetic.main.activity_splash.*


/**
 * Created by Al on 10/10/2018
 */

class SplashActivity : AmDraftActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        initData()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        handler(500)
    }

    private fun handler(duration: Long) {
        Handler().postDelayed({
            startAnActivity(layoutParent)
        }, duration)
    }

    private fun startAnActivity(v: View) {
        val bounds = Rect()
        v.getDrawingRect(bounds)

        var intent = Intent(context, SignInActivity::class.java)

        if(App.fireBaseAuth.currentUser != null) {
            intent = Intent(context, MainActivity::class.java)
            intent.putExtra(Constants.INTENT.LOGIN.FIRST_LOGIN, false)

            AmMessagingService().storeOnline(true)
            startService(Intent(baseContext, AmTaskService::class.java))

            Preferences(this).storeFirstLoadKegiatan(true)
            Preferences(this).storeFirstLoadSatker(true)

            if(this.intent.extras != null) {
                if(!this.intent.getStringExtra("google.delivered_priority").isNullOrEmpty() &&
                        this.intent.getStringExtra("title").isNotEmpty()) {
                    intent.putExtra(Constants.INTENT.LOGIN.PUSH.MESSAGE, this.intent.getStringExtra("fromUser"))
                    intent.putExtra(Constants.INTENT.LOGIN.PUSH.SATKER, this.intent.getStringExtra("satker"))
                    intent.putExtra(Constants.INTENT.LOGIN.PUSH.NAME, this.intent.getStringExtra("title"))
                    intent.putExtra(Constants.INTENT.LOGIN.PUSH.DESCRIPTION, this.intent.getStringExtra("description"))
                    intent.putExtra(Constants.INTENT.LOGIN.PUSH.DATE, this.intent.getStringExtra("date"))
                    intent.putExtra(Constants.INTENT.LOGIN.PUSH.ID, this.intent.getStringExtra("google.message_id"))
                }
            }


        }else{
            msg!!.unsubscribeFromTopic(getString(R.string.default_channel))
        }

        intent.putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_X, bounds.centerX())
        intent.putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_Y, bounds.centerY())

        ActivityCompat.startActivity(context, intent, null)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    override fun onBackPressed() {
        return
    }
}