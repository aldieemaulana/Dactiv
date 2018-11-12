package com.ismealdi.dactiv.activity.kegiatan.detail

import android.content.Context
import android.text.format.DateFormat
import com.google.firebase.firestore.*
import com.ismealdi.dactiv.App
import com.ismealdi.dactiv.activity.kegiatan.detail.DetailKegiatanPresenter.Companion.INFO.DB_USER_DETAIL_NOT_FOUND
import com.ismealdi.dactiv.activity.kegiatan.detail.DetailKegiatanPresenter.Companion.INFO.DB_USER_DETAIL_NOT_MATCH
import com.ismealdi.dactiv.activity.kegiatan.detail.DetailKegiatanPresenter.Companion.INFO.INPUT_NOT_COMPLETE
import com.ismealdi.dactiv.activity.kegiatan.detail.DetailKegiatanPresenter.Companion.INFO.USER_SUCCESS_ATTEND
import com.ismealdi.dactiv.activity.kegiatan.detail.DetailKegiatanPresenter.Companion.INFO.USER_SUCCESS_DONE
import com.ismealdi.dactiv.model.*
import com.ismealdi.dactiv.util.alert
import com.ismealdi.dactiv.util.kegiatan
import com.ismealdi.dactiv.util.user
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by Al on 20/10/2018
 */

class DetailKegiatanPresenter(private val view: DetailKegiatanContract.View, val context: Context) : DetailKegiatanContract.Presenter {

    private var database = App.fireStoreBase
    private val user = App.fireBaseAuth.currentUser
    private var kegiatanSnapshot : ListenerRegistration? = null
    private var userSnapshot : ListenerRegistration? = null

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

        if (barcode != kegiatan.id){
            view.onError(DB_USER_DETAIL_NOT_MATCH)
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

    override fun setAsDone(kegiatan: Kegiatan, realisasi: String, jadwal: String, deskripsi: String) {
        if (user == null){
            view.onError(DB_USER_DETAIL_NOT_FOUND)
            return
        }

        if(jadwal.isEmpty() || realisasi.toLong() < 1) {
            view.onError(INPUT_NOT_COMPLETE)
        }else{
            val jad = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(jadwal)
            val dat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(DateFormat.format("dd/MM/yyyy", kegiatan.jadwal).toString())
            kegiatan.status = if(dat.time < jad.time) 4 else 2
            kegiatan.realisasi = realisasi.toLong()
            kegiatan.pelaksanaan = jad

            if(dat.time < jad.time) {
                kegiatan.alasan = deskripsi

                if(deskripsi.isEmpty()) {
                    view.onError(INPUT_NOT_COMPLETE)
                    return
                }
            }


            val document = App.fireStoreBase.kegiatan(kegiatan.id)
            document.set(kegiatan).addOnSuccessListener {
                view.onError(USER_SUCCESS_DONE)
                view.onDoneKegiatan()
            }.addOnFailureListener {
                view.onError(it.message.toString())
            }
        }
    }

    private fun numberOfDays(start: Long, end: Long) : Long {
        val duration = end - start

        return TimeUnit.MILLISECONDS.toDays(duration)
    }

    override fun users(bagian: String) {
        if (user == null){
            view.onError(DB_USER_DETAIL_NOT_FOUND)
            return
        }

        view.progress.show()

        val  mUsers : MutableList<User> = mutableListOf()
        userSnapshot = database.user().addSnapshotListener (MetadataChanges.INCLUDE) { documentSnapshot, e ->

            if (documentSnapshot != null && e == null) {
                mUsers.clear()

                documentSnapshot.documents.forEach {
                    val mUser = it.toObject(User::class.java)

                    if (mUser != null) {
                        mUsers.add(mUser)
                    }
                }

                if(mUsers != null) {
                    view.mUsers = mUsers
                    view.populateAttendent(mUsers)
                }

                view.progress.dismiss()
            }

        }
    }

    override fun attendents(kegiatan: Kegiatan) {
        if (user == null){
            view.onError(DB_USER_DETAIL_NOT_FOUND)
            return
        }

        view.progress.show()

        val  mUsers : MutableList<Attendent> = mutableListOf()
        kegiatanSnapshot = database.kegiatan(kegiatan.id).addSnapshotListener (MetadataChanges.INCLUDE) { documentSnapshot, e ->

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

                view.progress.dismiss()
            }

        }

    }

    override fun killSnapshot() {
        if(kegiatanSnapshot != null)
            kegiatanSnapshot!!.remove()

        if(userSnapshot != null)
            userSnapshot!!.remove()
    }

    companion object {
        object INFO {
            const val DB_USER_DETAIL_NOT_MATCH = "Sorry the data not match!"
            const val USER_SUCCESS_ATTEND = "Mark as attend"
            const val USER_SUCCESS_DONE = "Mark as done"
            const val DB_USER_DETAIL_NOT_FOUND = "Sorry your detail not found on our record"
            const val INPUT_NOT_COMPLETE = "Please Fill in all detail"
        }
    }

}