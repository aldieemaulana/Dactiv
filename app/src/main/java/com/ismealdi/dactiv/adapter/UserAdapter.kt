package com.ismealdi.dactiv.adapter

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.components.AmTextView
import com.ismealdi.dactiv.model.Attendent
import com.ismealdi.dactiv.model.User
import kotlinx.android.synthetic.main.list_attendent.view.*
import java.util.*

class UserAdapter(private var users: MutableList<Attendent>, private var mUsers: MutableList<User>) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private var attendentPosition = ""
    private val dateFormat = "d MMMM yyyy"

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: AmTextView = itemView.textName
        val number: AmTextView = itemView.textPosition
        val time: AmTextView = itemView.textTime
        val frame: LinearLayout = itemView.layoutFrame
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_attendent, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[holder.adapterPosition]

        if(position == 0)
            this.attendentPosition = ""

        if(mUsers.size > 0) {
            val userd = mUsers.find { it.uid == user.user }
            if(userd != null && userd.displayName != "") {
                holder.name.setTextFade(userd.displayName)
            }else{
                holder.name.setTextFade("...")
            }

            val attendOn = DateFormat.format(dateFormat, user.attendOn.toDate()).toString()

            if(this.attendentPosition != attendOn) {
                this.attendentPosition = attendOn

                holder.number.setTextFade(this.attendentPosition)
                holder.number.visibility = View.VISIBLE
            }else{
                holder.number.visibility = View.GONE
            }

            holder.time.text = DateFormat.format("hh:mm", user.attendOn.toDate()).toString()
            holder.frame.setOnClickListener {

            }
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

    fun updateUser(usersData: MutableList<User>) {
        mUsers.clear()

        mUsers.addAll(usersData)
        notifyDataSetChanged()
    }
}