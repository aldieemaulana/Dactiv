package com.ismealdi.dactiv.adapter

import android.annotation.SuppressLint
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.activity.MainActivity
import com.ismealdi.dactiv.components.AmButton
import com.ismealdi.dactiv.components.AmTextView
import com.ismealdi.dactiv.model.Kegiatan
import kotlinx.android.synthetic.main.list_kegiatan.view.*
import java.text.NumberFormat
import java.util.*

class KegiatanAdapter(private var kegiatans: MutableList<Kegiatan>, private val context: MainActivity, private val actionAdd : View.OnClickListener) : RecyclerView.Adapter<KegiatanAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val description: AmTextView = itemView.textName
        val name: AmTextView = itemView.textTime
        val category: AmTextView = itemView.textCategory
        val state: AmTextView = itemView.textState
        val layoutLine: LinearLayout = itemView.layoutLine
        val frame: ConstraintLayout = itemView.layoutFrame
        val layoutView: RelativeLayout = itemView.layoutView
        val lineFirst: View = itemView.viewLineFirst
        val lineLast: View = itemView.viewLine
        val circle: View = itemView.viewCircle
        var format: NumberFormat? = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_kegiatan, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val kegiatan = kegiatans[holder.adapterPosition]
        val status = arrayOf("", "Belum Dilaksanakan", "Tepat Waktu Dilaksanakan", "Mendekati Waktu Dilaksanakan", "Terlambat Dilaksanakan")

        if(position == (kegiatans.size - 1))
            holder.lineLast.visibility = View.INVISIBLE
        else
            holder.lineLast.visibility = View.VISIBLE

        if(kegiatan.id != "0") {
            holder.name.setTextFade(kegiatan.name)
            holder.category.setTextFade(holder.format!!.format(kegiatan.anggaran))
            holder.frame.alpha = 1f

            if(kegiatan.status == -1) {
                holder.frame.alpha = .4f
                holder.state.visibility = View.INVISIBLE
                holder.description.setTextFade("Belum Terupload")
            }else{
                holder.state.visibility = View.VISIBLE
                holder.description.setTextFade(status[kegiatan.status])
                holder.frame.setOnClickListener {
                    // TODO Action
                }
            }
        }


    }

    override fun getItemCount(): Int {
        return kegiatans.size
    }

    fun updateData(mKegiatans: MutableList<Kegiatan>) {
        this.kegiatans.clear()
        this.kegiatans = mKegiatans
        notifyDataSetChanged()
    }

}
