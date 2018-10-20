package com.ismealdi.dactiv.activity.satker.detail

import android.content.Context
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.ismealdi.dactiv.App
import com.ismealdi.dactiv.activity.satker.detail.DetailSatkerPresenter.Companion.INFO.DB_KEGIATAN_DETAIL_NOT_FOUND
import com.ismealdi.dactiv.activity.satker.detail.DetailSatkerPresenter.Companion.INFO.DB_USER_DETAIL_NOT_FOUND
import com.ismealdi.dactiv.base.AmDraftActivity.Companion.kegiatanFields
import com.ismealdi.dactiv.model.Kegiatan
import com.ismealdi.dactiv.util.kegiatan


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

        val userDocument = database.kegiatan().whereEqualTo(kegiatanFields.satker, id).orderBy(kegiatanFields.createdOn, Query.Direction.DESCENDING)
        val  mKegiatans : MutableList<Kegiatan> = mutableListOf()

        userDocument.addSnapshotListener (MetadataChanges.INCLUDE) { documentSnapshot, _ ->
            view.progress.dismiss()

            if (documentSnapshot != null) {
                mKegiatans.clear()

                documentSnapshot.documents.forEach {
                    val mKegiatan = it.toObject(Kegiatan::class.java)

                    if (mKegiatan != null) {
                        if(mKegiatan.bagian == bagian.toString() || mKegiatan.admin == user.uid) {
                            mKegiatan.status = if(it.metadata.hasPendingWrites()) -1 else mKegiatan.status
                            mKegiatans.add(mKegiatan)
                        }
                    }
                }

                view.mKegiatan = mKegiatans
                view.populateList()

            }

        }
    }

    companion object {
        object INFO {
            const val DB_USER_DETAIL_NOT_FOUND = "Sorry your detail not found on our record"
            const val DB_KEGIATAN_DETAIL_NOT_FOUND = "Belum ada Kegiatan untuk Satuan Kerja"
        }
    }

}