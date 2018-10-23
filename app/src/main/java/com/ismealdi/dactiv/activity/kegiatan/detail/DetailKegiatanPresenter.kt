package com.ismealdi.dactiv.activity.kegiatan.detail

import android.content.Context
import android.text.format.DateFormat
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.ismealdi.dactiv.App
import com.ismealdi.dactiv.activity.kegiatan.detail.DetailKegiatanPresenter.Companion.INFO.DB_KEGIATAN_DETAIL_NOT_FOUND
import com.ismealdi.dactiv.activity.kegiatan.detail.DetailKegiatanPresenter.Companion.INFO.DB_USER_DETAIL_NOT_FOUND
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

class DetailKegiatanPresenter(private val view: DetailKegiatanContract.View, val context: Context) : DetailKegiatanContract.Presenter {

    init {
        view.presenter = this
    }

    companion object {
        object INFO {
            const val DB_USER_DETAIL_NOT_FOUND = "Sorry your detail not found on our record"
            const val DB_KEGIATAN_DETAIL_NOT_FOUND = "Belum ada Kegiatan untuk Satuan Kerja"
        }
    }

}