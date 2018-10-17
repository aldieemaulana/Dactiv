package com.ismealdi.dactiv.adapter

import android.app.Dialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.components.AmTextView
import kotlinx.android.synthetic.main.list_dialog.view.*

class DialogListAdapter(private val listData: List<String>, private var context: Context, private var dialog: Dialog, private var component: AmTextView) : RecyclerView.Adapter<DialogListAdapter.ViewHolder>() {

    private var selected: String = ""

    override fun getItemCount(): Int {
        return listData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: AmTextView = itemView.textValue
        val layoutParent: LinearLayout = itemView.parentLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_dialog, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(selected == listData[position]) {
            holder.textView.setBackgroundDrawable(context.resources.getDrawable(R.drawable.dialog_selected))
            holder.textView.setTextColor(context.resources.getColor(R.color.colorWhite))
        }

        holder.textView.text = listData[position]
        holder.layoutParent.setOnClickListener{
            actionClick(position)
        }
    }

    private fun actionClick(position: Int) {
        component.text = listData[position]
        component.setSelectedId(position)
        dialog.dismiss()
    }

    fun selected(selected: String) {
        this.selected = selected
    }
}
