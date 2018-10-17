package com.ismealdi.dactiv.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.activity.MainActivity
import com.ismealdi.dactiv.activity.profile.EditProfileActivity
import com.ismealdi.dactiv.base.AmFragment
import com.ismealdi.dactiv.model.User
import com.ismealdi.dactiv.util.CircleTransform
import com.ismealdi.dactiv.util.Constants
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : AmFragment() {

    private lateinit var mActivity : MainActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listener()
    }

    fun setData(user: User) {

        if(user.displayName != "") {
            if(textName != null) textName.setTextFade(user.displayName)
            if(textState != null) textState.setTextFade(if(user.emailVerified) "Verified" else "Unverified")
            if(textState != null) textEmail.setTextFade(user.email)

            val photo = user.photoUrl

            if(!photo.isNullOrEmpty() && imagePhoto != null) {
                Picasso.get().load(photo)
                        .transform(CircleTransform())
                        .placeholder(R.drawable.empty_circle)
                        .into(imagePhoto)
            }

            if(buttonEdit != null) buttonEdit.isEnabled = true
        }else{
            if(buttonEdit != null) buttonEdit.isEnabled = false
        }
    }

    private fun listener() {
        buttonEdit.setOnClickListener {
            startActivityForResult(Intent(context, EditProfileActivity::class.java), Constants.INTENT.ACTIVITY.EDIT_PROFILE)
        }

        buttonSignOut.setOnClickListener {
            mActivity.onSignOut()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        mActivity = activity as MainActivity
    }

    fun setGolongan(nama: String) {
        if(textGolongan != null) textGolongan.setTextFade(nama)
    }

    fun setJabatan(nama: String) {
        if(textJabatan != null) textJabatan.setTextFade(nama)
    }
}
