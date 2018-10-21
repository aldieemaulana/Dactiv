package com.ismealdi.dactiv.adapter

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.components.AmTextView
import com.ismealdi.dactiv.model.User
import kotlinx.android.synthetic.main.list_user.view.*

class EselonAdapter(private var users: MutableList<User>) : RecyclerView.Adapter<EselonAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: AmTextView = itemView.textName
        val onlineState: View = itemView.viewOnline
        val frame: LinearLayout = itemView.layoutFrame
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_user, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[holder.adapterPosition]

        holder.name.setTextFade(user.displayName)

        holder.onlineState.visibility = if(user.onlineUser) View.VISIBLE else View.GONE

    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun updateData(mUsers: MutableList<User>) {
        users.clear()
        users.addAll(mUsers)
        notifyDataSetChanged()
    }
}