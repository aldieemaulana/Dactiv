package com.ismealdi.dactiv.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.MenuItem
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.activity.signin.SignInActivity
import com.ismealdi.dactiv.activity.profile.ProfileActivity
import com.ismealdi.dactiv.base.AmDraftActivity
import com.ismealdi.dactiv.fragment.KegiatanFragment
import com.ismealdi.dactiv.fragment.MainFragment
import com.ismealdi.dactiv.fragment.ProfileFragment
import com.ismealdi.dactiv.fragment.SatkerFragment
import com.ismealdi.dactiv.model.*
import com.ismealdi.dactiv.services.AmMessagingService
import com.ismealdi.dactiv.services.AmTaskService
import com.ismealdi.dactiv.util.*
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Created by Al on 10/10/2018
 */

class MainActivity : AmDraftActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private val mainFragment = MainFragment()
    private val kegiatanFragment = KegiatanFragment()
    private val eventFragment = Fragment()
    private val satkerFragment = SatkerFragment()
    private val profileFragment = ProfileFragment()
    private var mRevealAnimation : RevealAnimation? = null

    internal var mUser : User? = null
    internal var activeFragment : Fragment = mainFragment
    internal val mFragmentManager = supportFragmentManager

    private var userSnapshot : ListenerRegistration? = null
    private var golonganSnapshot : ListenerRegistration? = null
    private var jabatanSnapshot : ListenerRegistration? = null
    private var satkerSnapshot : ListenerRegistration? = null
    private var kegiatanSnapshot : ListenerRegistration? = null

    override fun onConnectionChange(message: String) {
        showSnackBar(layoutSnackBar, message, Snackbar.LENGTH_SHORT, 850)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                setTitle(getString(R.string.title_home))
                mFragmentManager.beginTransaction().hide(activeFragment).show(mainFragment).commit()
                activeFragment = mainFragment

                return true
            }
            R.id.meeting -> {
                setTitle(getString(R.string.title_kegiatan))
                mFragmentManager.beginTransaction().hide(activeFragment).show(kegiatanFragment).commit()
                activeFragment = kegiatanFragment

                return true
            }
            R.id.event -> {
                setTitle(getString(R.string.title_event))
                mFragmentManager.beginTransaction().hide(activeFragment).show(eventFragment).commit()
                activeFragment = eventFragment

                return true
            }
            R.id.satker -> {
                setTitle(getString(R.string.title_satker))
                mFragmentManager.beginTransaction().hide(activeFragment).show(satkerFragment).commit()
                activeFragment = satkerFragment

                return true
            }
            R.id.profile -> {
                setTitle(getString(R.string.title_profile), true)
                mFragmentManager.beginTransaction().hide(activeFragment).show(profileFragment).commit()
                activeFragment = profileFragment

                return true
            }
        }

        return false
    }

    private fun setFragment() {
        mFragmentManager.beginTransaction().add(R.id.frameLayout, profileFragment, Constants.FRAGMENT.PROFILE.NAME).hide(profileFragment).commit()
        mFragmentManager.beginTransaction().add(R.id.frameLayout, satkerFragment, Constants.FRAGMENT.SATKER.NAME).hide(satkerFragment).commit()
        mFragmentManager.beginTransaction().add(R.id.frameLayout, eventFragment, Constants.FRAGMENT.EVENT.NAME).hide(eventFragment).commit()
        mFragmentManager.beginTransaction().add(R.id.frameLayout, kegiatanFragment, Constants.FRAGMENT.MEETING.NAME).hide(kegiatanFragment).commit()
        mFragmentManager.beginTransaction().add(R.id.frameLayout, mainFragment, Constants.FRAGMENT.MAIN.NAME).commit()

        setTitle(getString(R.string.title_home))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initData(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        setFragment()
        mRevealAnimation = RevealAnimation(layoutParent, intent, context as Activity)
        listener()

        getRealTimeProfile()
        getRealTimeSatker()
        getRealTimeKegiatan()

        if((auth?.currentUser?.displayName).isNullOrEmpty()) {
            bottomNavigation.selectedItemId = R.id.profile
            startActivityForResult(Intent(context, ProfileActivity::class.java), Constants.INTENT.ACTIVITY.EDIT_PROFILE)
        }
    }

    private fun listener() {
        bottomNavigation.setOnNavigationItemSelectedListener(this)
    }

    fun onSignOut() {
        if(msg != null) {
            msg!!.unsubscribeFromTopic(getString(R.string.default_channel))
            msg!!.unsubscribeFromTopic(mUser!!.bagian.toString())
        }

        AmMessagingService().storeOnline(false)
        auth?.signOut()
        mRevealAnimation?.unRevealActivity(Intent(context, SignInActivity::class.java), context)

    }

    private fun getRealTimeProfile() {

        userSnapshot = db?.user(user?.uid.toString())!!.addSnapshotListener { documentSnapshot, _ ->

            if (documentSnapshot != null && documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(User::class.java)
                if(user != null) {
                    if(user.category != mUser?.category) {
                        getRealTimeKegiatan()
                    }

                    mUser = user
                    mainFragment.setName(user.displayName)
                    profileFragment.setData(user)
                    satkerFragment.updateStateOfUser(user.category)
                    msg!!.subscribeToTopic(user.bagian.toString())

                    getRealTimeKegiatan()
                    getRealTimeGolongan(user.golongan.toString())
                    getRealTimeJabatan(user.bagian.toString())

                }

                hideProgress()
            }

        }
    }

    private fun getRealTimeGolongan(id: String) {

        golonganSnapshot = db?.golongan(id)!!.addSnapshotListener { documentSnapshot, _ ->

            if (documentSnapshot != null && documentSnapshot.exists()) {
                val golongan = documentSnapshot.toObject(Golongan::class.java)
                if (golongan != null) {
                    profileFragment.setGolongan(golongan.nama)
                }
            }

        }
    }

    private fun getRealTimeJabatan(id: String) {

        jabatanSnapshot = db?.jabatan(id)!!.addSnapshotListener { documentSnapshot, _ ->

            if (documentSnapshot != null && documentSnapshot.exists()) {
                val jabatan = documentSnapshot.toObject(Jabatan::class.java)
                if (jabatan != null) {
                    profileFragment.setJabatan(jabatan.nama)
                }
            }

        }
    }

    private fun getRealTimeSatker() {

        val mSatkers : MutableList<Satker> = mutableListOf()

        satkerSnapshot = db?.satker()?.orderBy(satkerFields.createdOn, Query.Direction.DESCENDING)!!.addSnapshotListener (MetadataChanges.INCLUDE) { documentSnapshot, _ ->

            if (documentSnapshot != null) {
                mSatkers.clear()

                documentSnapshot.documents.forEach {
                    val mSatker = it.toObject(Satker::class.java)

                    if (mSatker != null) {
                        var eselon = 0
                        val id = user?.uid
                        mSatker.eselon.forEach {
                            ++eselon
                        }

                        if(eselon > 0 || mSatker.kepala == id) {
                            mSatker.admin = if(it.metadata.hasPendingWrites()) "-1" else mSatker.admin
                            mSatkers.add(mSatker)
                        }
                    }
                }

                satkerFragment.updateList(mSatkers)
            }

        }
    }

    private fun getRealTimeKegiatan() {

        val  mKegiatans : MutableList<Kegiatan> = mutableListOf()

        kegiatanSnapshot = db?.kegiatan()?.orderBy(kegiatanFields.jadwal, Query.Direction.DESCENDING)!!.addSnapshotListener(MetadataChanges.INCLUDE) { documentSnapshot, _ ->

            if (documentSnapshot != null) {
                mKegiatans.clear()

                documentSnapshot.documents.forEach {
                    val mKegiatan = it.toObject(Kegiatan::class.java)

                    if (mKegiatan != null) {
                        if(mKegiatan.bagian == mUser?.bagian.toString()) {
                            mKegiatan.status = if(it.metadata.hasPendingWrites()) -1 else mKegiatan.status
                            mKegiatans.add(mKegiatan)
                        }
                    }
                }

                mainFragment.resetList(mKegiatans)
                kegiatanFragment.updateList(mKegiatans)
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == Constants.INTENT.ACTIVITY.EDIT_PROFILE && resultCode == Constants.INTENT.SUCCESS) {
            bottomNavigation.selectedItemId = R.id.profile
        }else if(requestCode == Constants.INTENT.ACTIVITY.ADD_SATKER && resultCode == Constants.INTENT.SUCCESS) {
            bottomNavigation.selectedItemId = R.id.satker
        }else if(requestCode == Constants.INTENT.ACTIVITY.ADD_KEGIATAN && resultCode == Constants.INTENT.SUCCESS) {
            bottomNavigation.selectedItemId = R.id.home
        }else if(requestCode == Constants.INTENT.ACTIVITY.ADD_KEGIATAN_MAIN && resultCode == Constants.INTENT.SUCCESS) {
            bottomNavigation.selectedItemId = R.id.meeting
        }
    }

    override fun onBackPressed() {
        AmMessagingService().storeOnline(false)
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()

        if(kegiatanSnapshot != null)
            kegiatanSnapshot!!.remove()
        if(satkerSnapshot != null)
            satkerSnapshot!!.remove()
        if(jabatanSnapshot != null)
            jabatanSnapshot!!.remove()
        if(golonganSnapshot != null)
            golonganSnapshot!!.remove()
        if(userSnapshot != null)
            userSnapshot!!.remove()
    }

}
