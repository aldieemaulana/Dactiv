package com.ismealdi.dactiv.activity.satker.detail

import com.ismealdi.dactiv.base.AmPresenter
import com.ismealdi.dactiv.base.AmView
import com.ismealdi.dactiv.model.Kegiatan
import com.ismealdi.dactiv.model.Satker

/**
 * Created by Al on 19/10/2018
 */

interface DetailSatkerContract {

    interface View : AmView<Presenter> {

        var mKegiatan : MutableList<Kegiatan>

        fun onSuccess(message: String)

        fun onError(message: String)

        fun populateList()

    }

    interface Presenter : AmPresenter {

        fun kegiatans(id: String, bagian: Int)

    }


}
