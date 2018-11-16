package com.ismealdi.dactiv.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.activity.MainActivity
import com.ismealdi.dactiv.activity.kegiatan.AddKegiatanActivity
import com.ismealdi.dactiv.adapter.KegiatanAdapter
import com.ismealdi.dactiv.base.AmActivity
import com.ismealdi.dactiv.base.AmFragment
import com.ismealdi.dactiv.model.Kegiatan
import com.ismealdi.dactiv.util.Constants
import kotlinx.android.synthetic.main.fragment_kegiatan.*
import kotlinx.android.synthetic.main.list_kegiatan_add.*
import kotlinx.android.synthetic.main.toolbar_primary.*
import kotlinx.android.synthetic.main.view_empty_state.*


class KegiatanFragment : AmFragment() {

    private lateinit var mActivity : MainActivity
    private lateinit var mAdapter : KegiatanAdapter

    private var mKegiatansFiltered : MutableList<Kegiatan> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textTitleToolbar.text = getString(R.string.title_kegiatan)

        initList()
        listener()
    }

    private fun listener() {
        buttonAdd.setOnClickListener {
            doAdd()
        }

        buttonAddMore.setOnClickListener {
            doAdd()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_kegiatan, container, false)
    }

    private fun initList() {
        mActivity.showProgress()

        mAdapter = KegiatanAdapter(mActivity, mKegiatansFiltered,false,true, listener = mActivity)

        recyclerView.layoutManager = LinearLayoutManager(context,
                LinearLayout.VERTICAL, false)
        recyclerView.adapter = mAdapter

        mActivity.hideProgress()

        showEmpty((mAdapter.itemCount == 0))
    }

    private fun doAdd() {
        startActivityForResult(Intent(context, AddKegiatanActivity::class.java), Constants.INTENT.ACTIVITY.ADD_KEGIATAN_MAIN)
    }

    private fun showEmpty(b: Boolean, delay: Int = 0) {
        if(b) {
            if(layoutEmpty != null) layoutEmpty.visibility = View.VISIBLE
            if(recyclerView != null) recyclerView.visibility = View.GONE
            if(layoutAddMore != null) layoutAddMore.visibility = View.GONE
        }else{
            if(layoutEmpty != null) layoutEmpty.visibility = View.GONE
            if(recyclerView != null) recyclerView.visibility = View.VISIBLE
            if(layoutAddMore != null) layoutAddMore.visibility = View.VISIBLE
        }

        Handler().postDelayed({
            loader(false)
        }, delay.toLong())

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        mActivity = activity as MainActivity
    }

    internal fun resetList(mKegiatans: MutableList<Kegiatan>) {
        mAdapter.updateData(mKegiatans)
        showEmpty((mKegiatansFiltered.size == 0), Constants.SHARED.defaultDelay)
    }


    fun loader(b: Boolean) {
        if(viewLoader != null) {
            if((viewLoader.visibility == View.GONE && b) || (viewLoader.visibility == View.VISIBLE && !b))
                viewLoader.visibility = if (b) View.VISIBLE else View.GONE
        }
    }
}
