package com.ismealdi.dactiv.activity.satker.detail

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.format.DateFormat
import android.view.View
import android.widget.LinearLayout
import com.ismealdi.dactiv.App
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.activity.MessageActivity
import com.ismealdi.dactiv.activity.kegiatan.AddKegiatanActivity
import com.ismealdi.dactiv.adapter.EselonAdapter
import com.ismealdi.dactiv.adapter.KegiatanAdapter
import com.ismealdi.dactiv.base.AmActivity
import com.ismealdi.dactiv.model.Kegiatan
import com.ismealdi.dactiv.model.Satker
import com.ismealdi.dactiv.model.User
import com.ismealdi.dactiv.util.Constants
import com.ismealdi.dactiv.util.Constants.INTENT.DETAIL_SATKER
import com.ismealdi.dactiv.util.Constants.INTENT.DETAIL_SATKER_BAGIAN
import com.ismealdi.dactiv.util.Dialogs
import com.ismealdi.dactiv.util.Utils
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_satker_detail.*
import kotlinx.android.synthetic.main.toolbar_primary.*
import java.util.*


/**
 * Created by Al on 20/10/2018
 */

class DetailSatkerActivity : AmActivity(), DetailSatkerContract.View {

    internal lateinit var mSatker : Satker

    override lateinit var progress: KProgressHUD
    override lateinit var presenter: DetailSatkerContract.Presenter
    override var mKegiatan : MutableList<Kegiatan> = mutableListOf()
    override var mEselons : MutableList<User> = mutableListOf()

    private lateinit var mAdapter : KegiatanAdapter
    private lateinit var mEselonAdapter : EselonAdapter


    fun init() {
        mSatker = intent.getParcelableExtra(DETAIL_SATKER)

        progress = Dialogs.initProgressDialog(this)
        presenter = DetailSatkerPresenter(this, this)

        initList()

        presenter.kegiatans(mSatker.id, intent.getIntExtra(DETAIL_SATKER_BAGIAN, 0))
        presenter.users(mSatker.eselon)

        listener()

        setTitle(getString(R.string.title_detail))
        buttonBackToolbar.visibility = View.VISIBLE
        buttonMenuToolbar.visibility = View.VISIBLE
        buttonMenuToolbar.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_plus))

        textName.setTextFade(mSatker.name)
        textKodeKegiatan.setTextFade(Utils.stringKodeFormat(mSatker.kodeSatker))
        textDescription.setTextFade(mSatker.description)
        textDate.setTextFade(DateFormat.format("d MMMM yyyy hh:mm", mSatker.createdOn.toDate()).toString())
    }

    private fun initList() {
        mAdapter = KegiatanAdapter(this, mKegiatan, true, true)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext,
                LinearLayout.VERTICAL, false)
        recyclerView.adapter = mAdapter

        mEselonAdapter = EselonAdapter(mEselons, this)
        recyclerViewEselon.layoutManager = LinearLayoutManager(applicationContext,
                LinearLayout.HORIZONTAL, false)
        recyclerViewEselon.adapter = mEselonAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initData(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_satker_detail)
        init()
    }

    override fun populateList(mKegiatans: MutableList<Kegiatan>) {
        mAdapter.updateData(mKegiatans)
        showEmpty((mKegiatans.size == 0))
    }

    override fun populateEselonList(eselons: MutableList<User>) {
        mEselonAdapter.updateData(eselons)
    }

    override fun onError(message: String) {
        showSnackBar(message)
    }

    override fun onSuccess(message: String) {
        showSnackBar(message, Snackbar.LENGTH_LONG)
    }

    private fun listener() {
        buttonMenuToolbar.setOnClickListener {
            val mIntent = Intent(applicationContext, AddKegiatanActivity::class.java)
            mIntent.putExtra(DETAIL_SATKER, mSatker)
            startActivityForResult(mIntent, Constants.INTENT.ACTIVITY.ADD_KEGIATAN)
        }

        buttonBackToolbar.setOnClickListener {
            onBackPressed()
        }
    }

    private fun showEmpty(b: Boolean) {
        if(b) {
            if(layoutEmpty != null) layoutEmpty.visibility = View.VISIBLE
            if(recyclerView != null) recyclerView.visibility = View.GONE
        }else{
            if(layoutEmpty != null) layoutEmpty.visibility = View.GONE
            if(recyclerView != null) recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == Constants.INTENT.ACTIVITY.ADD_KEGIATAN && resultCode == Constants.INTENT.SUCCESS) {
            presenter.kegiatans(mSatker.id, intent.getIntExtra(DETAIL_SATKER_BAGIAN, 0))
        }
    }

    fun openMessage(user: User) {
        if(App.fireBaseAuth.currentUser!!.uid != user.uid) {
            val mIntent = Intent(this, MessageActivity::class.java)

            mIntent.putExtra(Constants.INTENT.LOGIN.PUSH.SATKER, mSatker.id)
            mIntent.putExtra(Constants.INTENT.LOGIN.PUSH.MESSAGE, user.uid)
            mIntent.putExtra(Constants.INTENT.LOGIN.PUSH.NAME, user.displayName)
            mIntent.putExtra(Constants.INTENT.LOGIN.PUSH.DESCRIPTION, "")
            mIntent.putExtra(Constants.INTENT.LOGIN.PUSH.DATE, DateFormat.format("d MMMM yyyy h:m", Calendar.getInstance()).toString())

            startActivity(mIntent)
        }
    }

}