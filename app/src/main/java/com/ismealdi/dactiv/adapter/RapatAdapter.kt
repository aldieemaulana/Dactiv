package com.ismealdi.dactiv.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.activity.rapat.detail.DetailRapatActivity
import com.ismealdi.dactiv.components.AmTextView
import com.ismealdi.dactiv.model.Rapat
import com.ismealdi.dactiv.util.Constants.INTENT.DETAIL_RAPAT
import com.ismealdi.dactiv.util.Utils.numberOfDays
import kotlinx.android.synthetic.main.list_kegiatan.view.*
import java.text.NumberFormat
import java.util.*

class RapatAdapter(private var context: Context, private var rapats: MutableList<Rapat>, private var isFirstOff: Boolean = false, private var isTitled: Boolean = false) : RecyclerView.Adapter<RapatAdapter.ViewHolder>() {

    private var headOf = ""

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: AmTextView = itemView.textName
        val description: AmTextView = itemView.textDescription
        val category: AmTextView = itemView.textCategory
        val state: AmTextView = itemView.textState
        val layoutLine: LinearLayout = itemView.layoutLine
        val frame: ConstraintLayout = itemView.layoutFrame
        val layoutView: RelativeLayout = itemView.layoutView
        val lineFirst: View = itemView.viewLineFirst
        val lineLast: View = itemView.viewLine
        val lineTop: View = itemView.lineTop
        val lineBottom: View = itemView.lineBottom
        val circle: View = itemView.viewCircle
        val layoutTitle: LinearLayout = itemView.layoutTitle
        val date: AmTextView = itemView.textDate
        var format: NumberFormat? = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_kegiatan, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rapat = rapats[holder.adapterPosition]
        val status = arrayOf("", "Belum Dilaksanakan", "Tepat Waktu Dilaksanakan", "Mendekati Waktu Dilaksanakan", "Terlambat Dilaksanakan", "Tertindak Lanjuti dan Terlambat")

        if(position == 0 && isFirstOff)
            holder.lineFirst.visibility = View.INVISIBLE
        else
            holder.lineFirst.visibility = View.VISIBLE

        if(position == (rapats.size - 1))
            holder.lineLast.visibility = View.INVISIBLE
        else
            holder.lineLast.visibility = View.VISIBLE

        if(rapat.id != "0") {
            if(position == 0 && isFirstOff){
                holder.lineTop.visibility = View.GONE
                holder.lineBottom.visibility = View.GONE
            }

            holder.name.text = rapat.agendaRapat
            holder.category.text = ""
            holder.frame.alpha = 1f

            val day = DateFormat.format("d MMMM yyyy",  rapat.tanggalRapat)

            if(isTitled && headOf != day) {
                headOf = day.toString()
                holder.layoutTitle.visibility = View.VISIBLE
                holder.date.text = headOf
            }else{
                holder.layoutTitle.visibility = View.GONE
            }


            if(rapat.status == -1) {
                holder.frame.alpha = .4f
                holder.state.visibility = View.INVISIBLE
                holder.description.text = "Belum Terupload"
                holder.frame.setOnClickListener(null)
            }else{
                holder.state.visibility = View.VISIBLE
                holder.description.text = status[rapat.status]

                when {
                    rapat.status == 2 -> {
                        holder.circle.background = ContextCompat.getDrawable(context, R.drawable.state_green)
                        holder.state.background = ContextCompat.getDrawable(context, R.drawable.state_green)
                    }
                    rapat.status == 5 && rapat.tanggalBatasTindakLanjut != rapat.createdOn -> {
                        holder.circle.background = ContextCompat.getDrawable(context, R.drawable.state_red)
                        holder.state.background = ContextCompat.getDrawable(context, R.drawable.state_red)
                    }
                    rapat.status == 4 && rapat.tanggalBatasTindakLanjut == rapat.createdOn -> {
                        holder.circle.background = ContextCompat.getDrawable(context, R.drawable.state_red)
                        holder.state.background = ContextCompat.getDrawable(context, R.drawable.state_red)
                    }
                    else -> setStatus(holder.state, holder.circle, rapat.tanggalRapat.time, (rapat.tanggalBatasTindakLanjut == rapat.createdOn), rapat.tanggalBatasTindakLanjut.time, holder.description)
                }

                holder.frame.setOnClickListener {
                    detail(rapat)
                }
            }
        }
    }

    private fun detail(rapat: Rapat) {
        val mIntent = Intent(context, DetailRapatActivity::class.java)

        mIntent.putExtra(DETAIL_RAPAT, rapat)

        context.startActivity(mIntent)
    }

    @SuppressLint("SetTextI18n")
    private fun setStatus(state: AmTextView, circle: View, time: Long, status: Boolean, timeBatas: Long = 0, textView: AmTextView) {
        var dayLeft = numberOfDays(time)

        if (!status) {
            dayLeft = numberOfDays(timeBatas)
            textView.text = "Butuh Tindak Lanjut"
        }


        when {
            dayLeft in 1..14 -> {
                circle.background = ContextCompat.getDrawable(context, R.drawable.state_orange)
                state.background = ContextCompat.getDrawable(context, R.drawable.state_orange)
            }
            dayLeft <= 0-> {
                circle.background = ContextCompat.getDrawable(context, R.drawable.state_red)
                state.background = ContextCompat.getDrawable(context, R.drawable.state_red)
            }
            else -> {
                circle.background = ContextCompat.getDrawable(context, R.drawable.state_blue)
                state.background = ContextCompat.getDrawable(context, R.drawable.state_blue)
            }
        }
    }

    override fun getItemCount(): Int {
        return rapats.size
    }

    fun updateData(mRapats: MutableList<Rapat>) {
        rapats.clear()
        rapats.addAll(mRapats)
        notifyDataSetChanged()

        headOf = ""
    }

}
