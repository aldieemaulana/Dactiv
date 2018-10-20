package com.ismealdi.dactiv.activity.satker.add

import android.content.Context
import com.google.firebase.firestore.DocumentSnapshot
import com.ismealdi.dactiv.App
import com.ismealdi.dactiv.activity.satker.add.AddSatkerPresenter.Companion.VALIDATE.INPUT_EMPTY
import com.ismealdi.dactiv.model.Satker
import com.ismealdi.dactiv.model.User
import com.ismealdi.dactiv.util.satker
import com.ismealdi.dactiv.util.user
import java.util.HashMap

/**
 * Created by Al on 19/10/2018
 */

class AddSatkerPresenter(private val view: AddSatkerContract.View, val context: Context) : AddSatkerContract.Presenter {

    private var database = App.fireStoreBase
    private val user = App.fireBaseAuth.currentUser
    private var mUsers : MutableList<User> = mutableListOf()
    private var mDataAdmin : HashMap<String, String> = hashMapOf()

    init {
        view.presenter = this
    }

    override fun validateInput(kepalaId: Int?, eselons: Int?, nama: String, description: String, kodeKegiatan: String) {

        if (kepalaId != null && eselons != null &&
                kodeKegiatan.isNotEmpty() && nama.isNotEmpty() && description.isNotEmpty()) {

            val satker = Satker()

            satker.admin = user?.uid.toString()
            satker.kepala = mUsers[kepalaId].uid
            satker.eselon = ("${mUsers[eselons].uid},${satker.kepala}").split(",")
            satker.name = nama
            satker.description = description
            satker.kodeSatker = kodeKegiatan

            store(satker)

        }else {
            view.onError(INPUT_EMPTY)
        }
    }

    override fun store(satker: Satker) {
        val document = database.satker().document()

        satker.id = document.id

        document.set(satker).addOnFailureListener {
            view.onError(it.message.toString())
        }

        view.clearData()
    }

    override fun users() {
        database.user().addSnapshotListener { it, e ->
            if(e != null) {
                view.onError(e.message.toString())
            }

            if (it != null && it.documents.isNotEmpty()) {
                it.documents.forEach { document: DocumentSnapshot? ->
                    if(document != null && document.exists()) {
                        val mUser = document.toObject(User::class.java)
                        if(mUser != null) {
                            mDataAdmin[mUser.uid] = mUser.displayName
                            mUsers.add(mUser)
                        }
                    }
                }

                if(mDataAdmin.isNotEmpty()) {
                    view.populateDialog(mDataAdmin)
                }

            }
        }
    }

    companion object {
        object VALIDATE {
            const val  INPUT_EMPTY = "Please check your input data."
        }
    }

}
