package com.ismealdi.dactiv.activity.satker.detail

import android.content.Context
import android.text.format.DateFormat
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.ismealdi.dactiv.App
import com.ismealdi.dactiv.activity.satker.detail.DetailSatkerPresenter.Companion.INFO.DB_KEGIATAN_DETAIL_NOT_FOUND
import com.ismealdi.dactiv.activity.satker.detail.DetailSatkerPresenter.Companion.INFO.DB_USER_DETAIL_NOT_FOUND
import com.ismealdi.dactiv.base.AmDraftActivity.Companion.kegiatanFields
import com.ismealdi.dactiv.model.Kegiatan
import com.ismealdi.dactiv.model.Message
import com.ismealdi.dactiv.model.Satker
import com.ismealdi.dactiv.model.User
import com.ismealdi.dactiv.util.kegiatan
import com.ismealdi.dactiv.util.message
import com.ismealdi.dactiv.util.user
import java.util.*


/**
 * Created by Al on 20/10/2018
 */

class DetailSatkerPresenter(private val view: DetailSatkerContract.View, val context: Context) : DetailSatkerContract.Presenter {

    private var database = App.fireStoreBase
    private val user = App.fireBaseAuth.currentUser

    init {
        view.presenter = this
    }

    override fun kegiatans(id: String, bagian: Int) {
        if (user == null){
            view.onError(DB_USER_DETAIL_NOT_FOUND)
            return
        }

        view.progress.show()

        val userDocument = database.kegiatan().whereEqualTo(kegiatanFields.satker, id).orderBy(kegiatanFields.jadwal, Query.Direction.DESCENDING)
        val  mKegiatans : MutableList<Kegiatan> = mutableListOf()

        userDocument.addSnapshotListener (MetadataChanges.INCLUDE) { documentSnapshot, e ->
            view.progress.dismiss()

            if (documentSnapshot != null && e == null) {
                mKegiatans.clear()

                documentSnapshot.documents.forEach {
                    val mKegiatan = it.toObject(Kegiatan::class.java)

                    if (mKegiatan != null) {
                        if(mKegiatan.bagian == bagian.toString()) {
                            mKegiatan.status = if(it.metadata.hasPendingWrites()) -1 else mKegiatan.status
                            mKegiatans.add(mKegiatan)
                        }
                    }
                }

                view.mKegiatan = mKegiatans
                view.populateList(mKegiatans)

            }

        }
    }

    override fun users(id: List<String>) {
        if (user == null){
            view.onError(DB_USER_DETAIL_NOT_FOUND)
            return
        }

        view.progress.show()

        val userDocument = database.user()
        val  mUsers : MutableList<User> = mutableListOf()

        userDocument.addSnapshotListener (MetadataChanges.INCLUDE) { documentSnapshot, e ->
            view.progress.dismiss()

            if (documentSnapshot != null && e == null) {
                mUsers.clear()

                documentSnapshot.documents.forEach {
                    val mUser = it.toObject(User::class.java)

                    if (mUser != null) {
                        if(id.contains(mUser.uid))
                            mUsers.add(mUser)
                    }
                }

                view.mEselons = mUsers
                view.populateEselonList(mUsers)

            }

        }
    }

    override fun message(user: User, satker: Satker) {
        //if(this.user!!.uid != user.uid) {
            view.progress.show()

            val mMessage = Message()

            mMessage.fromUser = this.user!!.uid
            mMessage.toUser = user.uid
            mMessage.toToken = user.pushId
            mMessage.description = "Hai ${user.displayName} this is me ${this.user.displayName} we just connected now"
            mMessage.title = this.user.displayName.toString()
            mMessage.date = DateFormat.format("d MMMM yyyy h:m", Calendar.getInstance()).toString()
            mMessage.satker = satker.id

            val document = database.message().document()
            document.set(mMessage).addOnSuccessListener {
                view.progress.dismiss()
            }.addOnFailureListener {
                view.onError(it.message.toString())
            }
        //}

    }

    companion object {
        object INFO {
            const val DB_USER_DETAIL_NOT_FOUND = "Sorry your detail not found on our record"
            const val DB_KEGIATAN_DETAIL_NOT_FOUND = "Belum ada Kegiatan untuk Satuan Kerja"
        }
    }

}