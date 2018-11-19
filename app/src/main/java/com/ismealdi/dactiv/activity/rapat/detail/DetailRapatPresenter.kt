package com.ismealdi.dactiv.activity.rapat.detail

import android.content.Context
import android.text.format.DateFormat
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.ismealdi.dactiv.App
import com.ismealdi.dactiv.activity.rapat.add.AddRapatPresenter.Companion.VALIDATE.INPUT_EMPTY
import com.ismealdi.dactiv.activity.rapat.detail.DetailRapatPresenter.Companion.INFO.DB_USER_DETAIL_NOT_FOUND
import com.ismealdi.dactiv.activity.rapat.detail.DetailRapatPresenter.Companion.INFO.DB_USER_DETAIL_NOT_MATCH
import com.ismealdi.dactiv.activity.rapat.detail.DetailRapatPresenter.Companion.INFO.INPUT_NOT_COMPLETE
import com.ismealdi.dactiv.activity.rapat.detail.DetailRapatPresenter.Companion.INFO.USER_SUCCESS_ATTEND
import com.ismealdi.dactiv.activity.rapat.detail.DetailRapatPresenter.Companion.INFO.USER_SUCCESS_DONE
import com.ismealdi.dactiv.model.Attendent
import com.ismealdi.dactiv.model.Rapat
import com.ismealdi.dactiv.model.User
import com.ismealdi.dactiv.util.rapat
import com.ismealdi.dactiv.util.user
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Al on 19/11/2018
 */

class DetailRapatPresenter(private val view: DetailRapatContract.View, val context: Context) : DetailRapatContract.Presenter {
    private var database = App.fireStoreBase
    private val user = App.fireBaseAuth.currentUser
    private var mUsers : HashMap<String, User> = hashMapOf()
    private var mDataAdmin : HashMap<String, String> = hashMapOf()

    private var rapatSnapshot : ListenerRegistration? = null
    private var userSnapshot : ListenerRegistration? = null

    init {
        view.presenter = this
    }

    override fun penanggungJawab(penanggungJawab: String) {
        if (user == null){
            view.onError(DB_USER_DETAIL_NOT_FOUND)
            return
        }

        val userDocument = App.fireStoreBase.user(penanggungJawab)
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

    override fun doAttend(barcode: String, rapat: Rapat) {
        if (user == null){
            view.onError(DB_USER_DETAIL_NOT_FOUND)
            return
        }

        if (barcode != rapat.code.toString()){
            view.onError(DB_USER_DETAIL_NOT_MATCH)
            return
        }

        val attendents = rapat.attendent.toMutableList()
        val attender = Attendent(user.uid, barcode)

        attender.attendanceNumber = 1

        attendents.add(attender)

        rapat.attendent = attendents

        val document = App.fireStoreBase.rapat(rapat.id)
        document.set(rapat).addOnSuccessListener {
            view.onError(USER_SUCCESS_ATTEND)
            view.onDoneAttend()
        }.addOnFailureListener {
            view.onError(it.message.toString())
        }
    }

    override fun setAsDone(rapat: Rapat, jadwal: String, deskripsi: String, status: Int, alasan: String) {
        if (user == null){
            view.onError(DB_USER_DETAIL_NOT_FOUND)
            return
        }

        if(jadwal.isEmpty() || deskripsi.isEmpty()) {
            view.onError(INPUT_NOT_COMPLETE)
        }else{

            if(rapat.status == 1) {
                val jad = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(jadwal)
                rapat.status = status
                rapat.tanggalBatasTindakLanjut = jad

                rapat.notulenRapat = deskripsi
            }else{
                val jad = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(jadwal)
                val dat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(DateFormat.format("dd/MM/yyyy", rapat.tanggalBatasTindakLanjut).toString())
                rapat.status = if(dat.time < jad.time) 5 else 2
                rapat.tanggalTindakLanjut = jad
                rapat.keteranganTindakLanjut = deskripsi

                if(dat.time < jad.time) {
                    rapat.alasan = alasan

                    if(alasan.isEmpty()) {
                        view.onError(INPUT_NOT_COMPLETE)
                        return
                    }
                }
            }

            if (deskripsi.isEmpty()) {
                view.onError(INPUT_NOT_COMPLETE)
                return
            }


            val document = App.fireStoreBase.rapat(rapat.id)
            document.set(rapat).addOnSuccessListener {
                view.onError(USER_SUCCESS_DONE)
                view.onDoneRapat()
            }.addOnFailureListener {
                view.onError(it.message.toString())
            }
        }
    }

    override fun users() {
        if (user == null){
            view.onError(DB_USER_DETAIL_NOT_FOUND)
            return
        }

        view.loader(true)

        val  mUsers : MutableList<User> = mutableListOf()
        userSnapshot = App.fireStoreBase.user().addSnapshotListener (MetadataChanges.INCLUDE) { documentSnapshot, e ->

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

                view.loader(false)
            }

        }
    }

    override fun attendents(rapat: Rapat) {
        if (user == null){
            view.onError(DB_USER_DETAIL_NOT_FOUND)
            return
        }

        view.loader(true)

        val  mUsers : MutableList<Attendent> = mutableListOf()
        rapatSnapshot = App.fireStoreBase.rapat(rapat.id).addSnapshotListener (MetadataChanges.INCLUDE) { documentSnapshot, e ->

            if (documentSnapshot != null && e == null) {
                val kegiatan = documentSnapshot.toObject(Rapat::class.java)
                if(kegiatan != null) {
                    view.setData(kegiatan)
                    mUsers.clear()

                    kegiatan.attendent.forEach { att: Attendent ->
                        mUsers.add(att)
                    }

                    view.mAttendents = mUsers
                    view.reloadAttendent(mUsers)
                }

                view.loader(false)
            }

        }
    }

    override fun killSnapshot() {
        if(rapatSnapshot != null)
            rapatSnapshot!!.remove()

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