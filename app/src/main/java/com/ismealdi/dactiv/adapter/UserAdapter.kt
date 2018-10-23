package com.ismealdi.dactiv.adapter

import android.annotation.SuppressLint
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.components.AmTextView
import com.ismealdi.dactiv.model.Attendent
import com.ismealdi.dactiv.model.User
import kotlinx.android.synthetic.main.list_attendent.view.*

class UserAdapter(private var users: MutableList<Attendent>, private var mUsers: MutableList<User>) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: AmTextView = itemView.textName
        val time: AmTextView = itemView.textTime
        val frame: ConstraintLayout = itemView.layoutFrame
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_attendent, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[holder.adapterPosition]

        holder.name.setTextFade(mUsers.find { it.uid == user.user }!!.displayName)
        holder.time.setTextFade(DateFormat.format("hh:mm", user.attendOn.toDate()).toString())

        holder.frame.setOnClickListener {

        }

    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun updateData(mUsers: MutableList<Attendent>) {
        users.clear()
        users.addAll(mUsers)
        notifyDataSetChanged()
    }
}