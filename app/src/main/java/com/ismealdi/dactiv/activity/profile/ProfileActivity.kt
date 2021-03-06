package com.ismealdi.dactiv.activity.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.ArrayAdapter
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.activity.profile.ProfilePresenter.Companion.INFO.DB_USER_DETAIL_NOT_FOUND
import com.ismealdi.dactiv.activity.profile.ProfilePresenter.Companion.VALIDATE.UPDATE_DATA_FIRST
import com.ismealdi.dactiv.base.AmActivity
import com.ismealdi.dactiv.model.User
import com.ismealdi.dactiv.util.CircleTransform
import com.ismealdi.dactiv.util.Constants.INTENT.ACTIVITY.REQUEST_SELECT_IMAGE_IN_ALBUM
import com.ismealdi.dactiv.util.Dialogs
import com.ismealdi.dactiv.util.Utils
import com.ismealdi.dactiv.watcher.AmFourDigitWatcher
import com.kaopiz.kprogresshud.KProgressHUD
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile_edit.*
import kotlinx.android.synthetic.main.toolbar_primary.*
import android.graphics.Bitmap
import android.os.Build
import android.os.Handler
import android.provider.MediaStore
import com.ismealdi.dactiv.util.Constants.INTENT.ACTIVITY.REQUEST_WRITE_PERMISSION
import java.io.ByteArrayOutputStream
import android.support.v4.content.ContextCompat



/**
 * Created by Al on 15/10/2018
 */

class ProfileActivity : AmActivity(), ProfileContract.View {

    override var canBack: Boolean = true
    override var uriPhoto: Uri? = null

    override lateinit var progress: KProgressHUD
    override lateinit var presenter: ProfileContract.Presenter

    fun init() {
        progress = Dialogs.initProgressDialog(this)
        presenter = ProfilePresenter(this, this)

        progress.show()
        presenter.golongan()

        watcher()
        listener()

        setTitle(getString(R.string.title_profile_edit))
        buttonBackToolbar.visibility = View.VISIBLE
        buttonMenuToolbar.visibility = View.VISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initData(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)
        init()
    }

    override fun setGolongan(golongans: ArrayAdapter<String>) {
        textGolongan.adapter = golongans
        presenter.jabatan()
    }

    override fun setJabatan(jabatans: ArrayAdapter<String>) {
        textJabatan.adapter = jabatans
        presenter.profile()
    }

    override fun onError(message: String) {
        showSnackBar(message)
    }

    override fun onSuccess(message: String) {
        uriPhoto = null
        showSnackBar(message, Snackbar.LENGTH_LONG)
    }

    override fun updateProfile(user: User) {
        progress.dismiss()
        if (user.email != "") {
            textDisplayName.setText(user.displayName)
            textEmail.setText(user.email)
            textNip.setText(user.nip)
            textPhoneNumber.setText(user.phoneNumber)

            val photo = user.photoUrl
            if (!photo.isNullOrEmpty() && imagePhoto != null) {
                Picasso.get().load(photo)
                    .transform(CircleTransform())
                    .placeholder(R.drawable.empty_circle)
                    .into(imagePhoto)
            }

            val gol = user.golongan
            textGolongan.setSelection(gol)

            val jab = user.bagian
            textJabatan.setSelection(jab)
        } else {
            onError(DB_USER_DETAIL_NOT_FOUND)
        }
    }

    private fun listener() {
        buttonChangePhoto.setOnClickListener {
            val permissionCheckStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permissionCheckStorage != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_WRITE_PERMISSION)
            } else {
                presenter.selectImage(packageManager, this)
            }
        }

        buttonMenuToolbar.setOnClickListener {
            val email = textEmail.text.toString()
            val fullName = textDisplayName.text.toString()
            val nip = textNip.text.toString()
            val phoneNumber = textPhoneNumber.text.toString()
            val golongan = textGolongan.selectedItemPosition
            val jabatan = textJabatan.selectedItemPosition

            presenter.validateInput(email, fullName, nip.toNumber(), phoneNumber, golongan, jabatan, uriPhoto)
        }

        buttonBackToolbar.setOnClickListener {
            onBackPressed()
        }
    }

    private fun watcher() {
        textNip.addTextChangedListener(AmFourDigitWatcher())
    }

    override fun onBackPressed() {
        if(canBack)
            super.onBackPressed()
        else
            onError(UPDATE_DATA_FIRST)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM && resultCode == RESULT_OK &&data != null) {
            progress.show()
            Picasso.get().load(data.data)
                    .transform(CircleTransform())
                    .placeholder(R.drawable.empty_circle)
                    .into(imagePhoto)

            Handler().postDelayed({
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, data.data)
                uriPhoto = Utils.getImageUri(applicationContext, Utils.compressBitmap(bitmap, 10))

                progress.dismiss()
            }, 1500)

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_WRITE_PERMISSION) {
            if (grantResults.isNotEmpty() && permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    buttonChangePhoto.performClick()
                }
            }
        }

    }
}
