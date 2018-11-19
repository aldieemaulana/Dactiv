package com.ismealdi.dactiv.activity.rapat.add

import com.ismealdi.dactiv.base.AmPresenter
import com.ismealdi.dactiv.base.AmView
import com.ismealdi.dactiv.model.Rapat

/**
 * Created by Al on 19/11/2018
 */

interface AddRapatContract {

    interface View : AmView<Presenter> {

        fun onSuccess(message: String)

        fun onError(message: String)

        fun clearData()

        fun populateDialog(data: HashMap<String, String>)

    }

    interface Presenter : AmPresenter {

        fun validateInput(agendaRapat: String, tanggalRapat: String?, admin: String?)

        fun store(rapat: Rapat)

        fun users()

        fun killSnapshot()

    }


}