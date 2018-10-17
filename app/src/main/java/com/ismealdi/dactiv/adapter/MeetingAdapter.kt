package com.ismealdi.dactiv.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.components.AmTextView
import com.ismealdi.dactiv.model.Satker
import kotlinx.android.synthetic.main.list_meeting.view.*
import java.text.SimpleDateFormat

class MeetingAdapter(private var satkers: MutableList<Satker>, private val context: Context) : RecyclerView.Adapter<MeetingAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val description: AmTextView = itemView.textName
        val name: AmTextView = itemView.textTime
        val category: AmTextView = itemView.textCategory
        val state: AmTextView = itemView.textState
        val frame: ConstraintLayout = itemView.layoutFrame
        val lineFirst: View = itemView.viewLineFirst
        val lineLast: View = itemView.viewLine
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_meeting, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val satker = satkers[holder.adapterPosition]

        if(holder.adapterPosition == 0)
            holder.lineFirst.visibility = View.INVISIBLE

        if(holder.adapterPosition == (satkers.size - 1))
            holder.lineLast.visibility = View.INVISIBLE

        holder.name.setTextFade(satker.name)
        holder.description.setTextFade(satker.description)
        holder.category.setTextFade(SimpleDateFormat("H:M").format(satker.createdOn.toDate()))

        holder.frame.setOnClickListener {
            // TODO
        }

    }

    override fun getItemCount(): Int {
        return satkers.size
    }

    fun updateData(mSatkers: MutableList<Satker>) {
        this.satkers.clear()
        this.satkers = mSatkers
        notifyDataSetChanged()
    }


    /*private fun slideState(holder: ViewHolder) {
        if (mSelectedHolder != null) {

            if(mSelectedHolder != holder)
                slideState(mSelectedHolder!!)
        }

        val collapseMargin = ((-33).px)

        val params: RelativeLayout.LayoutParams = holder.state.layoutParams as RelativeLayout.LayoutParams
        val animator = if(params.topMargin != collapseMargin) ValueAnimator.ofInt((-16).px, collapseMargin) else ValueAnimator.ofInt(collapseMargin, (-16).px)

        animator.addUpdateListener { valueAnimator ->
            params.topMargin = valueAnimator.animatedValue as Int
            holder.state.requestLayout()
        }

        animator.duration = 500
        animator.start()
    }*/

}
