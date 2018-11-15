package com.ismealdi.dactiv.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.activity.MainActivity
import com.ismealdi.dactiv.activity.satker.add.AddSatkerActivity
import com.ismealdi.dactiv.activity.satker.detail.DetailSatkerActivity
import com.ismealdi.dactiv.adapter.SatkerAdapter
import com.ismealdi.dactiv.base.AmFragment
import com.ismealdi.dactiv.model.Satker
import com.ismealdi.dactiv.util.Constants
import com.ismealdi.dactiv.util.Constants.INTENT.DETAIL_SATKER
import com.ismealdi.dactiv.util.Constants.INTENT.DETAIL_SATKER_BAGIAN
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.view_empty_state.*


class SatkerFragment : AmFragment() {

    private lateinit var mActivity : MainActivity
    private lateinit var mAdapter : SatkerAdapter

    private var mSatkers : MutableList<Satker> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textTitleToolbar.text = getString(R.string.title_satker)

        initGrid()
        listener()
    }

    private fun listener() {
        buttonAdd.setOnClickListener {
            startActivityForResult(Intent(context, AddSatkerActivity::class.java), Constants.INTENT.ACTIVITY.ADD_SATKER)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_satker, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        mActivity = activity as MainActivity
    }

    private fun initGrid() {
        val mCategory = if(mActivity.mUser != null) mActivity.mUser!!.category else 0

        mActivity.showProgress()

        mAdapter = SatkerAdapter(mSatkers, this, mCategory)

        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = mAdapter

        mActivity.hideProgress()

        showEmpty((mAdapter.itemCount == 0))

        updateStateOfUser(mCategory)
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

    fun updateList(mSatkersNew: MutableList<Satker>) {
        mSatkers.clear()
        mSatkers.addAll(mSatkersNew)
        mAdapter.updateData(mSatkersNew)
        showEmpty((mSatkers.size == 0))
    }

    fun updateStateOfUser(category: Int) {
        mAdapter.updateUser(category)

        if(buttonAdd != null) buttonAdd.visibility = if(category == 1) View.VISIBLE else View.GONE
    }

    fun detail(position: Int) {
        val mIntent = Intent(context, DetailSatkerActivity::class.java)

        mIntent.putExtra(DETAIL_SATKER, mSatkers[position])
        mIntent.putExtra(DETAIL_SATKER_BAGIAN, mActivity.mUser?.bagian)

        startActivity(mIntent)

    }

}
