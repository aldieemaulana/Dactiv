package com.ismealdi.dactiv.activity.satker.detail

import android.content.Intent
import android.os.Bundle
import android.os.Handler
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
import com.ismealdi.dactiv.activity.kegiatan.detail.DetailKegiatanActivity
import com.ismealdi.dactiv.adapter.EselonAdapter
import com.ismealdi.dactiv.adapter.KegiatanAdapter
import com.ismealdi.dactiv.base.AmActivity
import com.ismealdi.dactiv.listener.KegiatanListener
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
import kotlinx.android.synthetic.main.view_empty_state.*
import java.util.*


/**
 * Created by Al on 20/10/2018
 */

class DetailSatkerActivity : AmActivity(), DetailSatkerContract.View, KegiatanListener {

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
        buttonAdd.visibility = View.GONE
        buttonMenuToolbar.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_plus))

        textName.text = mSatker.name
        textKodeKegiatan.text = Utils.stringKodeFormat(mSatker.kodeSatker)
        textDescription.text = mSatker.description
        textDate.text = DateFormat.format("d MMMM yyyy hh:mm", mSatker.createdOn.toDate()).toString()

    }

    private fun initList() {
        mAdapter = KegiatanAdapter(this, mKegiatan, true, true, listener = this)
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

    override fun populateList(kegiatans: MutableList<Kegiatan>) {
        mAdapter.updateData(kegiatans)
        showEmpty((kegiatans.size == 0), Constants.SHARED.defaultDelay)
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

    private fun showEmpty(b: Boolean, delay: Int = 0) {
        if(b) {
            if(layoutEmpty != null) layoutEmpty.visibility = View.VISIBLE
            if(recyclerView != null) recyclerView.visibility = View.GONE
        }else{
            if(layoutEmpty != null) layoutEmpty.visibility = View.GONE
            if(recyclerView != null) recyclerView.visibility = View.VISIBLE
        }

        Handler().postDelayed({
            loader(false)
        }, delay.toLong())
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

    override fun goToDetail(kegiatan: Kegiatan, nameView: View, anggaranView: View) {
        val mIntent = Intent(this, DetailKegiatanActivity::class.java)

        mIntent.putExtra(Constants.INTENT.DETAIL_KEGIATAN, kegiatan)
        /*mIntent.putExtra("nameView", ViewCompat.getTransitionName(nameView))
        mIntent.putExtra("anggaranView", ViewCompat.getTransitionName(anggaranView))*/

        /*val p1= Pair.create(nameView, ViewCompat.getTransitionName(nameView)!!)
        val p2= Pair.create(anggaranView, ViewCompat.getTransitionName(anggaranView)!!)

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                p1,
                p2)

        startActivity(mIntent, options.toBundle())*/
        startActivity(mIntent)

    }

    override fun loader(boolean: Boolean) {
        if(viewLoader != null) {
            if((viewLoader.visibility == View.GONE && boolean) || (viewLoader.visibility == View.VISIBLE && !boolean))
                viewLoader.visibility = if (boolean) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.killSnapshot()
    }

}