package com.ismealdi.dactiv.activity.profile

import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.ArrayAdapter
import com.ismealdi.dactiv.base.AmPresenter
import com.ismealdi.dactiv.base.AmView
import com.ismealdi.dactiv.model.User

/**
 * Created by Al on 19/10/2018
 */

interface ProfileContract {

    interface View : AmView<Presenter> {

        var canBack: Boolean

        var uriPhoto: Uri?

        fun onSuccess(message: String)

        fun onError(message: String)

        fun updateProfile(user: User)

        fun setGolongan(golongans: ArrayAdapter<String>)

        fun setJabatan(jabatans: ArrayAdapter<String>)

    }

    interface Presenter : AmPresenter {

        fun validateInput(email: String, fullName: String, nip: String, phoneNumber: String, golongan: Int, jabatan: Int, uriPhoto: Uri? = null)

        fun update(user: User)

        fun upload(user: User, uriPhoto: Uri)

        fun golongan()

        fun jabatan()

        fun profile()

        fun selectImage(packageManager: PackageManager, activity: Activity)

    }

}