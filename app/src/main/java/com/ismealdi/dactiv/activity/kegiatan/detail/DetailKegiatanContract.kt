package com.ismealdi.dactiv.activity.kegiatan.detail

import com.ismealdi.dactiv.base.AmPresenter
import com.ismealdi.dactiv.base.AmView
import com.ismealdi.dactiv.model.Alert
import com.ismealdi.dactiv.model.Attendent
import com.ismealdi.dactiv.model.Kegiatan
import com.ismealdi.dactiv.model.User

/**
 * Created by Al on 19/10/2018
 */

interface DetailKegiatanContract {

    interface View : AmView<Presenter> {

        var mUsers : MutableList<User>
        var mAttendents : MutableList<Attendent>

        fun onError(message: String)

        fun displayPenanggunJawab(user: User)

        fun onDoneAttend()

        fun onDoneKegiatan()

        fun populateAttendent(users: MutableList<User>)

        fun reloadAttendent(mAttendents: MutableList<Attendent>)

        fun setData(kegiatan: Kegiatan)

    }

    interface Presenter : AmPresenter {

        fun penanggungJawab(penanggungJawab: String)

        fun doAttend(barcode: String, kegiatan: Kegiatan)

        fun remindTo(mMessage: Alert)

        fun setAsDone(kegiatan: Kegiatan)

        fun users(bagian: String)

        fun attendents(kegiatan: Kegiatan)

    }


}
