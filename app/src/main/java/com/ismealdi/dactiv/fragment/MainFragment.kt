package com.ismealdi.dactiv.fragment

import kotlin.coroutines.*;
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.activity.MainActivity
import com.ismealdi.dactiv.activity.kegiatan.AddKegiatanActivity
import com.ismealdi.dactiv.adapter.CalendarAdapter
import com.ismealdi.dactiv.adapter.KegiatanAdapter
import com.ismealdi.dactiv.base.AmFragment
import com.ismealdi.dactiv.model.Kegiatan
import com.ismealdi.dactiv.util.Constants
import com.ismealdi.dactiv.util.Constants.INTENT.SELECTED_DATE
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.list_kegiatan_add.*
import java.text.SimpleDateFormat
import java.util.*


class MainFragment : AmFragment() {

    private lateinit var mActivity : MainActivity
    private lateinit var mKegiatanAdapter : KegiatanAdapter
    private lateinit var mAdapterCalendar : CalendarAdapter

    private var mKegiatans : MutableList<Kegiatan> = mutableListOf()
    private var mKegiatansFiltered : MutableList<Kegiatan> = mutableListOf()

    internal var currentDay = 0

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
            doAdd()
        }

        buttonAddMore.setOnClickListener {
            doAdd()
        }
    }

    private fun initCalendar() {
        mActivity.showProgress()

        val data : MutableList<String> = mutableListOf()
        val date = Date()
        val cal = Calendar.getInstance()

        cal.time = date

        val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val day = SimpleDateFormat("d", Locale.ENGLISH)
        val name = SimpleDateFormat("EEEE", Locale.ENGLISH)
        val monthYear = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)

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

        mKegiatanAdapter = KegiatanAdapter(activity!!, mKegiatansFiltered)

        recyclerView.layoutManager = LinearLayoutManager(context,
                LinearLayout.VERTICAL, false)
        recyclerView.adapter = mKegiatanAdapter

        mActivity.hideProgress()

        showEmpty((mKegiatanAdapter.itemCount == 0))
    }

    private fun doAdd() {
        val intent = Intent(context, AddKegiatanActivity::class.java)
        intent.putExtra(SELECTED_DATE, currentDay)
        startActivityForResult(intent, Constants.INTENT.ACTIVITY.ADD_KEGIATAN)
    }

    private fun showEmpty(b: Boolean) {
        if(b) {
            if(layoutEmpty != null) layoutEmpty.visibility = View.VISIBLE
            if(recyclerView != null) recyclerView.visibility = View.GONE
            if(layoutAddMore != null) layoutAddMore.visibility = View.GONE
        }else{
            if(layoutEmpty != null) layoutEmpty.visibility = View.GONE
            if(recyclerView != null) recyclerView.visibility = View.VISIBLE
            if(layoutAddMore != null) layoutAddMore.visibility = View.VISIBLE
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

    private fun updateList() {
        mKegiatanAdapter.updateData(mKegiatansFiltered)
        showEmpty((mKegiatansFiltered.size == 0))
    }

    internal fun filterList(currentDay: Int) {

        val date = Date()
        date.date = currentDay

        val mDateFormat = SimpleDateFormat("d/M/yyyy", Locale.ENGLISH)
        val mKegiatansFiltered : MutableList<Kegiatan> = mutableListOf()

        mKegiatans.forEach {
            if (mDateFormat.format(it.jadwal) == mDateFormat.format(date)) {
                mKegiatansFiltered.add(it)
            }
        }

        this.mKegiatansFiltered.clear()
        this.mKegiatansFiltered = mKegiatansFiltered
        updateList()
    }

    internal fun resetList(mKegiatans: MutableList<Kegiatan>) {
        this.mKegiatans.clear()
        this.mKegiatans = mKegiatans

        filterList(currentDay)
    }
}
