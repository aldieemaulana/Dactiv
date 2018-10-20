package com.ismealdi.dactiv.activity.satker.detail

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.LinearLayout
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.adapter.KegiatanAdapter
import com.ismealdi.dactiv.base.AmActivity
import com.ismealdi.dactiv.model.Kegiatan
import com.ismealdi.dactiv.model.Satker
import com.ismealdi.dactiv.util.Constants.INTENT.DETAIL_SATKER
import com.ismealdi.dactiv.util.Constants.INTENT.DETAIL_SATKER_BAGIAN
import com.ismealdi.dactiv.util.Dialogs
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.fragment_kegiatan.*
import kotlinx.android.synthetic.main.toolbar_primary.*


/**
 * Created by Al on 20/10/2018
 */

class DetailSatkerActivity : AmActivity(), DetailSatkerContract.View {

    private lateinit var mSatker : Satker

    override lateinit var progress: KProgressHUD
    override lateinit var presenter: DetailSatkerContract.Presenter
    override var mKegiatan : MutableList<Kegiatan> = mutableListOf()

    private lateinit var mAdapter : KegiatanAdapter


    fun init() {
        mSatker = intent.getParcelableExtra(DETAIL_SATKER)

        progress = Dialogs.initProgressDialog(this)
        presenter = DetailSatkerPresenter(this, this)

        initList()

        presenter.kegiatans(mSatker.id, intent.getIntExtra(DETAIL_SATKER_BAGIAN, 0))

        listener()

        setTitle(mSatker.name)
        buttonBackToolbar.visibility = View.VISIBLE
    }

    private fun initList() {
        mAdapter = KegiatanAdapter(mKegiatan)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext,
                LinearLayout.VERTICAL, false)
        recyclerView.adapter = mAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initData(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_satker_detail)
        init()
    }

    override fun populateList() {
        mAdapter.updateData(mKegiatan)
        showEmpty((mAdapter.itemCount == 0))
    }

    override fun onError(message: String) {
        showSnackBar(message)
    }

    override fun onSuccess(message: String) {
        showSnackBar(message, Snackbar.LENGTH_LONG)
    }

    private fun listener() {

    }

    private fun showEmpty(b: Boolean) {
        if(b) {
            if(layoutEmpty != null) layoutEmpty.visibility = View.VISIBLE
            if(recyclerView != null) recyclerView.visibility = View.GONE
            if(layoutAddMore != null) layoutAddMore.visibility = View.GONE
        }else{
            if(layoutEmpty != null) layoutEmpty.visibility = View.GONE
            if(recyclerView != null) recyclerView.visibility = View.VISIBLE
            if(layoutAddMore != null) layoutAddMore.visibility = View.VISIBLE
        }
    }

}