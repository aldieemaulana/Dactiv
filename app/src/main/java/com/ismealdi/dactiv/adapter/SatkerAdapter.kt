package com.ismealdi.dactiv.adapter

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.components.AmButton
import com.ismealdi.dactiv.components.AmTextView
import com.ismealdi.dactiv.fragment.SatkerFragment
import com.ismealdi.dactiv.model.Satker
import com.ismealdi.dactiv.util.Utils
import kotlinx.android.synthetic.main.fragment_satker.*
import kotlinx.android.synthetic.main.list_satker.view.*
import kotlinx.android.synthetic.main.view_empty_state.*

class SatkerAdapter(private var satkers: MutableList<Satker>, private val context: SatkerFragment, private var category: Int = 0) : RecyclerView.Adapter<SatkerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val description: AmTextView = itemView.textDescription
        val name: AmTextView = itemView.textName
        val frame: LinearLayout = itemView.layoutFrame
        val more: RelativeLayout = itemView.layoutAddMore
        val buttonAdd: AmButton = itemView.buttonAdd
        val buttonMore: AmTextView = itemView.buttonMore
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_satker, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val satker = satkers[holder.adapterPosition]

        if(satker.id != "0") {
            holder.frame.visibility = View.VISIBLE
            holder.more.visibility = View.GONE
            holder.name.text = satker.name
            holder.description.text = satker.description
            holder.buttonMore.text = Utils.stringKodeFormat(satker.kodeSatker)
            holder.frame.alpha = 1f

            if(satker.admin == "-1") {
                holder.frame.alpha = .4f
                holder.buttonMore.text = "Belum Terupload"
            }else {
                holder.frame.setOnClickListener {
                    context.detail(holder.adapterPosition)
                }
            }

        }else{
            holder.frame.visibility = View.GONE
            holder.name.text = ""
            holder.description.text = ""
            holder.more.visibility = View.VISIBLE
            holder.buttonAdd.setOnClickListener {
                context.buttonAdd.performClick()
            }
        }

    }

    override fun getItemCount(): Int {
        return satkers.size
    }

    fun updateData(mSatkers: MutableList<Satker>) {
        this.satkers.clear()

        if(mSatkers.size > 0 && this.category > 0 && this.category == 1) {
            this.satkers.add(Satker("0"))
        }

        this.satkers.addAll(mSatkers)

        notifyDataSetChanged()
    }

    fun updateUser(category: Int) {
        this.category = category

        if(itemCount > 0) {
            if(this.category > 0 && this.category == 1 && this.satkers[0].id != "0") {
                this.satkers.add(0, Satker("0", ""))
                notifyDataSetChanged()
            }else if(this.satkers[0].id == "0" && this.category != 1) {
                this.satkers.removeAt(0)
                notifyDataSetChanged()
            }
        }
    }
}
