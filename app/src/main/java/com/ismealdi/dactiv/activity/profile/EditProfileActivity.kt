package com.ismealdi.dactiv.activity.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.ArrayAdapter
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.Timestamp
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentSnapshot
import com.ismealdi.dactiv.App
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.base.AmActivity
import com.ismealdi.dactiv.model.Golongan
import com.ismealdi.dactiv.model.Jabatan
import com.ismealdi.dactiv.model.User
import com.ismealdi.dactiv.util.*
import com.ismealdi.dactiv.util.Constants.INTENT.ACTIVITY.REQUEST_SELECT_IMAGE_IN_ALBUM
import com.ismealdi.dactiv.util.Constants.INTENT.ACTIVITY.REQUEST_TAKE_PHOTO
import com.ismealdi.dactiv.watcher.AmFourDigitWatcher
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile_edit.*
import kotlinx.android.synthetic.main.toolbar_primary.*


/**
 * Created by Al on 15/10/2018
 */

class EditProfileActivity : AmActivity() {

    object VALIDATE {
        const val  UPDATE_DATA_FIRST = "Please fill all data for first registered user."
        const val  INPUT_EMPTY = "Please check your input data."
        const val  INPUT_NO_DATA_CHANGE = "There is no data change."
        const val  DB_USER_DETAIL_NOT_FOUND = "\"Sorry your detail not found on our record\""
    }

    private var mCanBack = false
    private var uriPhoto : Uri? = null
    private var mUser: User = User()
    private var mDataGolongan : MutableList<String> = mutableListOf()
    private var mDataJabatan : MutableList<String> = mutableListOf()

    private val onFailureListener = OnFailureListener {
        showSnackBar(layoutParent, it.message.toString(), Snackbar.LENGTH_LONG, 100)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initData(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        init()
    }

    private fun listener() {
        buttonChangePhoto.setOnClickListener {
            selectImageInAlbum()
        }

        buttonMenuToolbar.setOnClickListener {
            validateInput()
        }

        buttonBackToolbar.setOnClickListener {
            onBackPressed()
        }
    }

    private fun uploadImage(data: User) {
        showProgress()

        val storageRef = storage?.reference?.child(Constants.PATH.PROFILE_PHOTO + user!!.uid)

        if(storageRef != null) {
            val uploadTask = storageRef.putFile(uriPhoto!!)

            uploadTask.addOnSuccessListener {

                storageRef.downloadUrl.addOnSuccessListener {
                    data.photoUrl = it.toString()
                    onUpdate(data)
                }.addOnFailureListener(onFailureListener)

            }.addOnFailureListener(onFailureListener)
        }
    }

    private fun validateInput() {
        val email = textEmail.text.toString()
        val fullName = textDisplayName.text.toString()
        val nip = textNip.text.toString().toNumber()
        val phoneNumber = textPhoneNumber.text.toString()
        val golongan = textGolongan.selectedItemPosition
        val jabatan = textJabatan.selectedItemPosition

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
                    uploadImage(data)
                }else{
                    onUpdate(data)
                }
            }else{
                showSnackBar(layoutParent, VALIDATE.INPUT_NO_DATA_CHANGE, Snackbar.LENGTH_LONG, 100)
            }
        } else {
            showSnackBar(layoutParent, VALIDATE.INPUT_EMPTY, Snackbar.LENGTH_LONG, 100)
        }
    }

    private fun onUpdate(data: User) {
        showProgress()

        data.lastUpdated = Timestamp.now()

        App.fireStoreBase.user(user!!.uid).set(data)
                .addOnCompleteListener {
                    updateFireBase(data)
                    mCanBack = true
                    showSnackBar(layoutParent, "Success updated", Snackbar.LENGTH_LONG, 0)
                }.addOnFailureListener(onFailureListener)
    }

    private fun updateFireBase(data: User) {
        val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(data.displayName)
                .setPhotoUri(Uri.parse(data.photoUrl))
                .build()

        user!!.updateProfile(profileUpdates).addOnFailureListener(onFailureListener)
    }

    private fun init() {
        initSpinner()
        getProfile()
        listener()
        watcher()

        setTitle(getString(R.string.title_profile_edit))
        buttonBackToolbar.visibility = View.VISIBLE
        buttonMenuToolbar.visibility = View.VISIBLE
    }

    private fun watcher() {
        textNip.addTextChangedListener(AmFourDigitWatcher())
    }

    private fun getProfile() {
        showProgress()

        val userDocument = db?.user(user?.uid.toString())

        userDocument!!.addSnapshotListener { it, e ->
            if(e != null) {
                showSnackBar(layoutParent, e.message.toString(), Snackbar.LENGTH_LONG)
            }

            if (it != null && it.exists()) {
                if(it.data != null) {
                    mUser = it.toObject(User::class.java)!!

                    if (mUser.email != "") {
                        textDisplayName.setText(mUser.displayName)
                        textEmail.setText(mUser.email)
                        textNip.setText(mUser.nip)
                        textPhoneNumber.setText(mUser.phoneNumber)

                        val photo = mUser.photoUrl

                        if (!photo.isNullOrEmpty() && imagePhoto != null) {
                            Picasso.get().load(photo)
                                    .transform(CircleTransform())
                                    .placeholder(R.drawable.empty_circle)
                                    .into(imagePhoto)
                        }

                        val gol = mUser.golongan
                        if(mDataGolongan.isNotEmpty() && mDataGolongan.size >= gol) {
                            textGolongan.setSelection(gol)
                        }

                        val jab = mUser.bagian
                        if(mDataJabatan.isNotEmpty() && mDataJabatan.size >= gol) {
                            textJabatan.setSelection(jab)
                        }

                        buttonMenuToolbar.isEnabled = true

                    } else {
                        buttonMenuToolbar.isEnabled = false

                        showSnackBar(layoutParent, VALIDATE.DB_USER_DETAIL_NOT_FOUND, Snackbar.LENGTH_LONG)
                    }
                }
                hideProgress()
            }
        }
    }

    private fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

    private fun takePhoto() {
        val intent1 = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent1.resolveActivity(packageManager) != null) {
            startActivityForResult(intent1, REQUEST_TAKE_PHOTO)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM && resultCode == RESULT_OK &&data != null) {
            showProgress()

            uriPhoto = data.data

            Picasso.get().load(uriPhoto)
                .transform(CircleTransform())
                .placeholder(R.drawable.empty_circle)
                .into(imagePhoto)

            hideProgress()
        }
    }


    override fun onBackPressed() {
        user?.reload()!!
        if(!(user?.displayName).isNullOrEmpty() || mCanBack) {
            setResult(Constants.INTENT.SUCCESS)
            super.onBackPressed()
        }else {
            showSnackBar(layoutParent, VALIDATE.UPDATE_DATA_FIRST, Snackbar.LENGTH_LONG)
        }
    }

    private fun initSpinner() {
        getGolongan()
        getJabatan()
    }

    private fun getGolongan() {
        showProgress()
        db!!.golongan().addSnapshotListener { it, e ->
            if(e != null) {
                showSnackBar(layoutParent, e.message.toString(), Snackbar.LENGTH_LONG)
            }

            if (it != null && it.documents.isNotEmpty()) {

                it.documents.forEach { document: DocumentSnapshot? ->
                    if(document != null && document.exists()) {
                        val golongan = document.toObject(Golongan::class.java)
                        if(golongan != null) {
                            mDataGolongan.add(golongan.nama)
                        }
                    }
                }

                if(mDataGolongan.isNotEmpty()) {
                    val arrayAdapter = ArrayAdapter(context, R.layout.am_spinner_dropdown_item, mDataGolongan.toTypedArray())
                    textGolongan.adapter = arrayAdapter
                }

            }

            hideProgress()
        }
    }

    private fun getJabatan() {
        showProgress()
        db!!.jabatan().addSnapshotListener { it, e ->
            if(e != null) {
                showSnackBar(layoutParent, e.message.toString(), Snackbar.LENGTH_LONG)
            }

            if (it != null && it.documents.isNotEmpty()) {
                it.documents.forEach { document: DocumentSnapshot? ->
                    if(document != null && document.exists()) {
                        val jabatan = document.toObject(Jabatan::class.java)
                        if(jabatan != null) {
                            mDataJabatan.add(jabatan.nama)
                        }
                    }
                }

                if(mDataJabatan.isNotEmpty()) {
                    val arrayAdapter = ArrayAdapter(context, R.layout.am_spinner_dropdown_item, mDataJabatan.toTypedArray())
                    textJabatan.adapter = arrayAdapter
                }

            }

            hideProgress()
        }
    }

}
