package com.ismealdi.dactiv.activity

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.LinearLayout
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
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.FirebaseMessaging
import com.ismealdi.dactiv.adapter.CalendarAdapter
import kotlinx.android.synthetic.main.fragment_main.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Al on 10/10/2018
 */

class SampleActivity : AmDraftActivity() {

    private lateinit var mAdapterCalendar : CalendarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        initData()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)
        initCalendar()
    }

    private fun initCalendar() {
        val data : MutableList<String> = mutableListOf()
        val date = Date()
        val cal = Calendar.getInstance()

        cal.time = date

        val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val day = SimpleDateFormat("d", Locale.ENGLISH)
        val name = SimpleDateFormat("EEEE", Locale.ENGLISH)
        val monthYear = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)

        textDay.text = name.format(cal.time)
        textMonthYear.text = monthYear.format(cal.time)

        for (i in 0 until maxDay) {
            cal.set(Calendar.DAY_OF_MONTH, i + 1)
            data.add(day.format(cal.time)+ "#" +name.format(cal.time))
        }

        mAdapterCalendar = CalendarAdapter(data.toList(), this)

        recyclerViewCalendar.layoutManager = LinearLayoutManager(context,
                LinearLayout.HORIZONTAL, false)
        recyclerViewCalendar.adapter = mAdapterCalendar
    }
}