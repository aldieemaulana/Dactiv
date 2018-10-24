package com.ismealdi.dactiv.activity.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.ArrayAdapter
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.Timestamp
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.ismealdi.dactiv.App
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.activity.profile.ProfilePresenter.Companion.INFO.DB_USER_DETAIL_NOT_FOUND
import com.ismealdi.dactiv.activity.profile.ProfilePresenter.Companion.INFO.USER_SUCCESS_UPDATED
import com.ismealdi.dactiv.activity.profile.ProfilePresenter.Companion.VALIDATE.INPUT_EMPTY
import com.ismealdi.dactiv.activity.profile.ProfilePresenter.Companion.VALIDATE.INPUT_NO_DATA_CHANGE
import com.ismealdi.dactiv.model.Golongan
import com.ismealdi.dactiv.model.Jabatan
import com.ismealdi.dactiv.model.User
import com.ismealdi.dactiv.util.Constants.INTENT.ACTIVITY.REQUEST_SELECT_IMAGE_IN_ALBUM
import com.ismealdi.dactiv.util.Constants.PATH.PROFILE_PHOTO
import com.ismealdi.dactiv.util.golongan
import com.ismealdi.dactiv.util.jabatan
import com.ismealdi.dactiv.util.user


/**
 * Created by Al on 19/10/2018
 */

class ProfilePresenter(private val view: ProfileContract.View, val context: Context) : ProfileContract.Presenter {

    private var database = App.fireStoreBase
    private val user = App.fireBaseAuth.currentUser
    private val storage = App.fireStorage
    private var mUser: User = User()
    private var mDataGolongan : MutableList<String> = mutableListOf()
    private var mDataJabatan : MutableList<String> = mutableListOf()

    private val onFailureListener = OnFailureListener {
        view.progress.dismiss()
        view.onError(it.message.toString())
    }

    init {
        view.presenter = this
    }

    override fun update(user: User) {
        view.progress.show()
        user.lastUpdated = Timestamp.now()

        database.user(user.uid).set(user)
        .addOnSuccessListener {
            view.progress.dismiss()
            updateAuth(user)
            view.canBack = true
            view.onSuccess(USER_SUCCESS_UPDATED)
        }.addOnFailureListener(onFailureListener)
    }

    private fun updateAuth(user: User) {
        if(this.user != null) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(user.displayName)
                    .setPhotoUri(Uri.parse(user.photoUrl))
                    .build()

            this.user.updateProfile(profileUpdates).addOnFailureListener(onFailureListener)
        }
    }

    override fun upload(user: User, uriPhoto: Uri) {
        view.progress.show()
        val storageRef = storage.reference.child(PROFILE_PHOTO + user.uid)
        val uploadTask = storageRef.putFile(uriPhoto)
        uploadTask.addOnSuccessListener { _ ->
            storageRef.downloadUrl.addOnSuccessListener {
                user.photoUrl = it.toString()
                update(user)
            }.addOnFailureListener(onFailureListener)

        }.addOnFailureListener(onFailureListener)
    }

    override fun validateInput(email: String, fullName: String, nip: String, phoneNumber: String, golongan: Int, jabatan: Int, uriPhoto: Uri?) {
        if(email.isNotBlank() && fullName.isNotBlank() && nip.isNotBlank() && phoneNumber.isNotBlank() ) {
            if(email != mUser.email  ||
                    fullName != mUser.displayName ||
                    nip != mUser.nip ||
                    phoneNumber != mUser.phoneNumber || uriPhoto != null ||
                    golongan != (mUser.golongan) || jabatan != mUser.bagian) {
                val data = mUser

                data.email = email
                data.displayName = fullName
                data.nip = nip
                data.phoneNumber = phoneNumber
                data.golongan = golongan
                data.bagian = jabatan

                if(uriPhoto != null) {
                    upload(data, uriPhoto)
                }else{
                    update(data)
                }
            }else{
                view.onError(INPUT_NO_DATA_CHANGE)
            }
        } else {
            view.onError(INPUT_EMPTY)
        }
    }

    override fun golongan() {
        database.golongan().get().addOnSuccessListener {
            if(it.documents.isNotEmpty()) {
                it.documents.forEach { document: DocumentSnapshot? ->
                    if(document != null && document.exists()) {
                        val golongan = document.toObject(Golongan::class.java)
                        if(golongan != null) {
                            this.mDataGolongan.add(golongan.nama)
                        }

                    }
                }

                val golongans = ArrayAdapter(context, R.layout.am_spinner_dropdown_item, this.mDataGolongan.toTypedArray())
                view.setGolongan(golongans)
            }

        }.addOnFailureListener(onFailureListener)
    }

    override fun jabatan() {
        database.jabatan().get().addOnSuccessListener {

            if(it.documents.isNotEmpty()) {
                it.documents.forEach { document: DocumentSnapshot? ->
                    if(document != null && document.exists()) {
                        val jabatan = document.toObject(Jabatan::class.java)
                        if(jabatan != null) {
                            this.mDataJabatan.add(jabatan.nama)
                        }
                    }
                }

                val jabatans = ArrayAdapter(context, R.layout.am_spinner_dropdown_item, this.mDataJabatan.toTypedArray())
                view.setJabatan(jabatans)
            }

        }.addOnFailureListener(onFailureListener)
    }

    override fun profile() {
        if(user != null) {
            val mUser = database.user(user.uid)
            mUser.get().addOnSuccessListener {
                if(it.data != null) {
                    this.mUser = it.toObject(User::class.java)!!
                    view.updateProfile(this.mUser)
                }

            }.addOnFailureListener(onFailureListener)



        }else{
            view.onError(DB_USER_DETAIL_NOT_FOUND)
        }
    }

    override fun selectImage(packageManager: PackageManager, activity: Activity) {
        val intent = Intent(ACTION_GET_CONTENT)
        intent.type = "image/*"

        if (intent.resolveActivity(packageManager) != null) {
            activity.startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

    companion object {
        object VALIDATE {
            const val UPDATE_DATA_FIRST = "Please fill all data for first registered user."
            const val INPUT_EMPTY = "Please check your input data."
            const val INPUT_NO_DATA_CHANGE = "There is no data change."
        }

        object ERROR {
            const val TOO_MANY_ATTEMP  = "Too many attempts, try again later or reset password"
        }

        object INFO {
            const val USER_SUCCESS_UPDATED = "Success Updated"
            const val DB_USER_DETAIL_NOT_FOUND = "Sorry your detail not found on our record"
        }
    }
}