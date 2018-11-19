package com.ismealdi.dactiv.listener

import android.view.View
import com.ismealdi.dactiv.components.AmTextView
import com.ismealdi.dactiv.model.Kegiatan

interface KegiatanListener {

    fun goToDetail(kegiatan: Kegiatan, nameView: View, anggaranView: View)

}
