package com.ismealdi.dactiv.activity.satker.add

import com.ismealdi.dactiv.base.AmPresenter
import com.ismealdi.dactiv.base.AmView
import com.ismealdi.dactiv.model.Satker

/**
 * Created by Al on 19/10/2018
 */

interface AddSatkerContract {

    interface View : AmView<Presenter> {

        fun onSuccess(message: String)

        fun onError(message: String)

        fun clearData()

        fun populateDialog(data: HashMap<String, String>)

    }

    interface Presenter : AmPresenter {

        fun validateInput(kepalaId: String?, eselons: String?, nama: String, description: String, kodeKegiatan: String)

        fun store(satker: Satker)

        fun users()

        fun killSnapshot()

    }


}
