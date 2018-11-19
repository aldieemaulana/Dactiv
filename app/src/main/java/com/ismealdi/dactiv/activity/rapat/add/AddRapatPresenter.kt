package com.ismealdi.dactiv.activity.rapat.add

import android.content.Context
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.ismealdi.dactiv.App
import com.ismealdi.dactiv.activity.rapat.add.AddRapatPresenter.Companion.VALIDATE.INPUT_EMPTY
import com.ismealdi.dactiv.model.Rapat
import com.ismealdi.dactiv.model.User
import com.ismealdi.dactiv.util.rapat
import com.ismealdi.dactiv.util.user
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Al on 19/11/2018
 */

class AddRapatPresenter(private val view: AddRapatContract.View, val context: Context) : AddRapatContract.Presenter {

    private var database = App.fireStoreBase
    private val user = App.fireBaseAuth.currentUser
    private var mUsers : HashMap<String, User> = hashMapOf()
    private var mDataAdmin : HashMap<String, String> = hashMapOf()

    private var userSnapshot : ListenerRegistration? = null

    init {
        view.presenter = this
    }

    override fun validateInput(agendaRapat: String, tanggalRapat: String?, admin: String?) {
        if (agendaRapat.isNotEmpty() && tanggalRapat != "" && admin != "" && admin != null) {
            val rapat = Rapat()

            rapat.admin = admin
            rapat.agendaRapat = agendaRapat
            rapat.tanggalRapat = SimpleDateFormat("dd/MM/yyyy h:m", Locale.ENGLISH).parse(tanggalRapat)
            rapat.status = 1

            store(rapat)
        }else {
            view.onError(INPUT_EMPTY)
        }
    }

    override fun store(rapat: Rapat) {
        val document = database.rapat().document()

        rapat.id = document.id

        document.set(rapat).addOnFailureListener {
            view.onError(it.message.toString())
        }

        view.clearData()
    }

    override fun users() {
        userSnapshot = database.user().addSnapshotListener { it, e ->
            if(e != null) {
                view.onError(e.message.toString())
            }

            if (it != null && it.documents.isNotEmpty()) {
                it.documents.forEach { document: DocumentSnapshot? ->
                    if(document != null && document.exists()) {
                        val mUser = document.toObject(User::class.java)
                        if(mUser != null) {
                            mDataAdmin[mUser.uid] = mUser.displayName
                            mUsers[mUser.uid] = mUser
                        }
                    }
                }

                if(mDataAdmin.isNotEmpty()) {
                    view.populateDialog(mDataAdmin)
                }

            }
        }
    }

    override fun killSnapshot() {
        if(userSnapshot != null)
            userSnapshot!!.remove()
    }

    companion object {
        object VALIDATE {
            const val  INPUT_EMPTY = "Please check your input data."
        }
    }
}