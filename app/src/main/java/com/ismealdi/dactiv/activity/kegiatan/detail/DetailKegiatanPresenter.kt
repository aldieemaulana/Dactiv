package com.ismealdi.dactiv.activity.kegiatan.detail

import android.content.Context
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.ismealdi.dactiv.App
import com.ismealdi.dactiv.activity.kegiatan.detail.DetailKegiatanPresenter.Companion.INFO.DB_USER_DETAIL_NOT_FOUND
import com.ismealdi.dactiv.activity.kegiatan.detail.DetailKegiatanPresenter.Companion.INFO.USER_SUCCESS_ATTEND
import com.ismealdi.dactiv.activity.kegiatan.detail.DetailKegiatanPresenter.Companion.INFO.USER_SUCCESS_DONE
import com.ismealdi.dactiv.base.AmDraftActivity.Companion.kegiatanFields
import com.ismealdi.dactiv.model.*
import com.ismealdi.dactiv.util.alert
import com.ismealdi.dactiv.util.kegiatan
import com.ismealdi.dactiv.util.user


/**
 * Created by Al on 20/10/2018
 */

class DetailKegiatanPresenter(private val view: DetailKegiatanContract.View, val context: Context) : DetailKegiatanContract.Presenter {

    private var database = App.fireStoreBase
    private val user = App.fireBaseAuth.currentUser

    init {
        view.presenter = this
    }

    override fun penanggungJawab(penanggungJawab: String) {
        if (user == null){
            view.onError(DB_USER_DETAIL_NOT_FOUND)
            return
        }

        val userDocument = database.user(penanggungJawab)
        val  mUsers : MutableList<User> = mutableListOf()

        userDocument.addSnapshotListener (MetadataChanges.INCLUDE) { documentSnapshot, e ->

            if (documentSnapshot != null && e == null) {
                mUsers.clear()

                val mUser = documentSnapshot.toObject(User::class.java)

                if (mUser != null) {
                    view.displayPenanggunJawab(mUser)
                }

            }

        }
    }

    override fun doAttend(barcode: String, kegiatan: Kegiatan) {
        if (user == null){
            view.onError(DB_USER_DETAIL_NOT_FOUND)
            return
        }

        val attendents = kegiatan.attendent.toMutableList()
        val attender = Attendent(user.uid, barcode)

        attendents.add(attender)

        kegiatan.attendent = attendents

        val document = App.fireStoreBase.kegiatan(kegiatan.id)
        document.set(kegiatan).addOnSuccessListener {
            view.onError(USER_SUCCESS_ATTEND)
            view.onDoneAttend()
        }.addOnFailureListener {
            view.onError(it.message.toString())
        }
    }

    override fun remindTo(mMessage: Alert) {
        val document = App.fireStoreBase.alert().document()
        document.set(mMessage).addOnFailureListener {
            view.onError(it.message.toString())
        }
    }

    override fun setAsDone(kegiatan: Kegiatan) {
        if (user == null){
            view.onError(DB_USER_DETAIL_NOT_FOUND)
            return
        }

        kegiatan.status = 2

        val document = App.fireStoreBase.kegiatan(kegiatan.id)
        document.set(kegiatan).addOnSuccessListener {
            view.onError(USER_SUCCESS_DONE)
            view.onDoneKegiatan()
        }.addOnFailureListener {
            view.onError(it.message.toString())
        }
    }

    override fun users(bagian: String) {
        if (user == null){
            view.onError(DB_USER_DETAIL_NOT_FOUND)
            return
        }


        val userDocument = database.user()
        val  mUsers : MutableList<User> = mutableListOf()

        userDocument.addSnapshotListener (MetadataChanges.INCLUDE) { documentSnapshot, e ->

            if (documentSnapshot != null && e == null) {
                mUsers.clear()

                documentSnapshot.documents.forEach {
                    val mUser = it.toObject(User::class.java)

                    if (mUser != null) {
                        if(mUser.bagian == bagian.toInt())
                            mUsers.add(mUser)
                    }
                }

                view.mUsers = mUsers
                view.populateAttendent(mUsers)
            }

        }
    }

    override fun attendents(kegiatan: Kegiatan) {
        if (user == null){
            view.onError(DB_USER_DETAIL_NOT_FOUND)
            return
        }

        val userDocument = database.kegiatan(kegiatan.id)
        val  mUsers : MutableList<Attendent> = mutableListOf()

        userDocument.addSnapshotListener (MetadataChanges.INCLUDE) { documentSnapshot, e ->

            if (documentSnapshot != null && e == null) {
                val kegiatan = documentSnapshot.toObject(Kegiatan::class.java)
                if(kegiatan != null) {
                    view.setData(kegiatan)
                    mUsers.clear()

                    kegiatan.attendent.forEach { att: Attendent ->
                        if (att != null) {
                            mUsers.add(att)
                        }
                    }

                    view.mAttendents = mUsers
                    view.reloadAttendent(mUsers)
                }
            }

        }
    }

    companion object {
        object INFO {
            const val USER_SUCCESS_ATTEND = "Mark as attend"
            const val USER_SUCCESS_DONE = "Mark as done"
            const val DB_USER_DETAIL_NOT_FOUND = "Sorry your detail not found on our record"
            const val DB_KEGIATAN_DETAIL_NOT_FOUND = "Belum ada Kegiatan untuk Satuan Kerja"
        }
    }

}