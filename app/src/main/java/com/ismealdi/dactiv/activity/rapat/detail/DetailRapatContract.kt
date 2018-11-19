package com.ismealdi.dactiv.activity.rapat.detail

import com.ismealdi.dactiv.base.AmPresenter
import com.ismealdi.dactiv.base.AmView
import com.ismealdi.dactiv.model.*

/**
 * Created by Al on 19/11/2018
 */

interface DetailRapatContract {

    interface View : AmView<Presenter> {

        var mUsers : MutableList<User>
        var mAttendents : MutableList<Attendent>

        fun onError(message: String)

        fun displayPenanggunJawab(user: User)

        fun onDoneAttend()

        fun onDoneRapat()

        fun populateAttendent(users: MutableList<User>)

        fun reloadAttendent(mAttendents: MutableList<Attendent>)

        fun setData(rapat: Rapat)

        fun loader(boolean: Boolean)

    }

    interface Presenter : AmPresenter {

        fun penanggungJawab(penanggungJawab: String)

        fun doAttend(barcode: String, rapat: Rapat)

        fun setAsDone(rapat: Rapat, jadwal: String, deskripsi: String, status: Int = 2, alasan: String = "")

        fun users()

        fun attendents(rapat: Rapat)

        fun killSnapshot()

    }


}