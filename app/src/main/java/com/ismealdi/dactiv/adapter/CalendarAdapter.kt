package com.ismealdi.dactiv.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.activity.MainActivity
import com.ismealdi.dactiv.fragment.MainFragment
import com.ismealdi.dactiv.util.Constants
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.list_calendar.view.*

class CalendarAdapter(private var dates: List<String>, private val context: Context) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: AppCompatTextView = itemView.textDate
        val name: AppCompatTextView = itemView.textName
        val frame: ConstraintLayout = itemView.layoutFrame
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_calendar, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val texts = dates[holder.adapterPosition].split("#")
        holder.date.text = texts[0]
        holder.name.text = texts[1].substring(0,3)

        holder.frame.setOnClickListener {
            changeDate(texts[0], texts[1])
        }

    }

    private fun changeDate(date: String, day: String) {
        context as MainActivity

        val mainFragment = context.mFragmentManager.findFragmentByTag(Constants.FRAGMENT.MAIN.NAME) as MainFragment
        if (mainFragment.isVisible) {
            mainFragment.textDate.setTextFade(date)
            mainFragment.textDay.setTextFade(day)
            mainFragment.currentDay = date.toInt()
            mainFragment.filterList(date.toInt())
        }

    }

    override fun getItemCount(): Int {
        return dates.size
    }

}
