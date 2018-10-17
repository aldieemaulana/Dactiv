package com.ismealdi.dactiv.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.activity.satker.AddSatkerActivity
import com.ismealdi.dactiv.activity.MainActivity
import com.ismealdi.dactiv.adapter.CalendarAdapter
import com.ismealdi.dactiv.adapter.MeetingAdapter
import com.ismealdi.dactiv.base.AmFragment
import com.ismealdi.dactiv.model.Satker
import com.ismealdi.dactiv.util.Constants
import com.ismealdi.dactiv.util.Constants.INTENT.ACTIVITY.ADD_SATKER
import com.ismealdi.dactiv.util.Constants.INTENT.SELECTED_DATE
import kotlinx.android.synthetic.main.fragment_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainFragment : AmFragment() {

    private lateinit var mActivity : MainActivity
    private lateinit var mAdapter : MeetingAdapter
    private lateinit var mAdapterCalendar : CalendarAdapter

    internal var currentDay = 0
    internal var mSatkers : MutableList<Satker> = mutableListOf()
    internal var mSatkersFiltered : MutableList<Satker> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initList()
        initCalendar()
        listener()
    }

    private fun listener() {
        textDate.setOnClickListener {
            setToCurrentDay()
        }

        buttonAdd.setOnClickListener {
            val intent = Intent(context, AddSatkerActivity::class.java)
            intent.putExtra(SELECTED_DATE, currentDay)
            startActivityForResult(intent, ADD_SATKER)
        }
    }

    private fun initCalendar() {
        mActivity.showProgress()

        val data : MutableList<String> = mutableListOf()
        val date = Date()
        val cal = Calendar.getInstance()

        cal.time = date

        val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val day = SimpleDateFormat("d")
        val name = SimpleDateFormat("EEEE")
        val monthYear = SimpleDateFormat("MMMM yyyy")

        currentDay = day.format(cal.time).toInt()

        textDate.text = currentDay.toString()
        textDay.text = name.format(cal.time)
        textMonthYear.text = monthYear.format(cal.time)

        for (i in 0 until maxDay) {
            cal.set(Calendar.DAY_OF_MONTH, i + 1)
            data.add(day.format(cal.time)+ "#" +name.format(cal.time))
        }

        mAdapterCalendar = CalendarAdapter(data.toList(), mActivity)

        recyclerViewCalendar.layoutManager = LinearLayoutManager(context,
                LinearLayout.HORIZONTAL, false)
        recyclerViewCalendar.adapter = mAdapterCalendar

        mActivity.hideProgress()

        recyclerViewCalendar.scrollToPosition(currentDay - 1)
    }

    private fun setToCurrentDay() {
        recyclerViewCalendar.smoothScrollToPosition(currentDay - 1)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    private fun initList() {
        mActivity.showProgress()

        mAdapter = MeetingAdapter(mSatkersFiltered, mActivity)

        recyclerView.layoutManager = LinearLayoutManager(context,
                LinearLayout.VERTICAL, false)
        recyclerView.adapter = mAdapter

        mActivity.hideProgress()

        showEmpty((mAdapter.itemCount == 0))
    }

    private fun showEmpty(b: Boolean) {
        if(b) {
            layoutEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }else{
            layoutEmpty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)

        mActivity = activity as MainActivity
    }

    fun setName(nama: String) {
        val name = if(nama.contains(" ")) nama.split(" ")[0] else nama

        if(textWelcome != null) textWelcome.setTextFade("Hai $name!")
    }

    private fun updateList(mSatkers: MutableList<Satker>) {
        mAdapter.updateData(mSatkers)
        showEmpty((mSatkers.size == 0))
    }

    internal fun filterList(currentDay: Int) {
        mActivity.showProgress()

        val date = Date()
        date.date = currentDay

        val mDateFormat = SimpleDateFormat("d/M/yyyy")
        val mSatkersFiltered : MutableList<Satker> = mutableListOf()

        mSatkers.forEach {
            if (mDateFormat.format(it.createdOn.toDate()) == mDateFormat.format(date)) {
                mSatkersFiltered.add(it)
            }
        }

        this.mSatkersFiltered.clear()
        this.mSatkersFiltered = mSatkersFiltered
        updateList(mSatkersFiltered)
        mActivity.hideProgress()
    }

    internal fun resetList(mSatkers: MutableList<Satker>) {
        this.mSatkers.clear()
        this.mSatkers = mSatkers

        filterList(currentDay)
    }
}
