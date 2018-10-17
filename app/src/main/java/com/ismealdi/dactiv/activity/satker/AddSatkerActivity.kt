package com.ismealdi.dactiv.activity.satker

import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.view.View
import com.google.firebase.firestore.DocumentSnapshot
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.activity.satker.AddSatkerActivity.Companion.VALIDATE.INPUT_EMPTY
import com.ismealdi.dactiv.base.AmActivity
import com.ismealdi.dactiv.model.Satker
import com.ismealdi.dactiv.model.User
import com.ismealdi.dactiv.util.Constants
import com.ismealdi.dactiv.util.satker
import com.ismealdi.dactiv.util.user
import kotlinx.android.synthetic.main.activity_satker_add.*
import kotlinx.android.synthetic.main.toolbar_primary.*
import java.util.*

class AddSatkerActivity : AmActivity() {

    companion object {
        object VALIDATE {
            const val  INPUT_EMPTY = "Please check your input data."
        }
    }

    private var mDataUsers : MutableList<User> = mutableListOf()
    private var mDataAdmin : HashMap<String, String> = hashMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        initData(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_satker_add)

        init()
    }

    private fun listener() {
        buttonBackToolbar.setOnClickListener {
            onBackPressed()
        }

        buttonMenuToolbar.setOnClickListener {
            validateInput()
        }
    }

    private fun validateInput() {
        val adminId = textAdmin.getSelectedId()
        val kepalaId = textKepala.getSelectedId()
        val eselons = textEselon.getSelectedId()
        val nama = textNamaSatker.text.toString()
        val description = textDeskripsi.text.toString()

        if (adminId != null && kepalaId != null && eselons != null &&
                nama.isNotEmpty() && description.isNotEmpty()) {

            val data = Satker()

            data.admin = mDataUsers[adminId].uid
            data.kepala = mDataUsers[kepalaId].uid
            data.eselon = ("${mDataUsers[eselons].uid},${data.admin},${data.kepala}").split(",")
            data.name = nama
            data.description = description

            addSatker(data)

        }else {
            showSnackBar(layoutParent, INPUT_EMPTY, Snackbar.LENGTH_LONG, 100)
        }
    }

    private fun addSatker(data: Satker) {
        showProgress()
        db!!.satker().add(data).addOnSuccessListener {
            showSnackBar(layoutParent, "Success data saved!", Snackbar.LENGTH_LONG)

            Handler().postDelayed({
                setResult(Constants.INTENT.SUCCESS)
                finish()
            }, 1500)

        }.addOnFailureListener {
            showSnackBar(layoutParent, it.message.toString(), Snackbar.LENGTH_LONG)
        }
    }

    private fun init() {
        listener()

        setTitle(getString(R.string.title_add_satker))
        buttonBackToolbar.visibility = View.VISIBLE
        buttonMenuToolbar.visibility = View.VISIBLE

        initView()
    }

    private fun initView() {
        getUsers()
    }

    private fun getUsers() {
        showProgress()
        db!!.user().addSnapshotListener { it, e ->
            if(e != null) {
                showSnackBar(layoutParent, e.message.toString(), Snackbar.LENGTH_LONG)
            }

            if (it != null && it.documents.isNotEmpty()) {
                it.documents.forEach { document: DocumentSnapshot? ->
                    if(document != null && document.exists()) {
                        val mUser = document.toObject(User::class.java)
                        if(mUser != null) {
                            mDataAdmin[mUser.uid] = mUser.displayName
                            mDataUsers.add(mUser)
                        }
                    }
                }

                if(mDataAdmin.isNotEmpty()) {
                    layoutAdmin.setDialogList(mDataAdmin)
                    layoutEselon.setDialogList(mDataAdmin)
                    layoutKepala.setDialogList(mDataAdmin)
                }

            }

            hideProgress()
        }
    }


}
