package com.ismealdi.dactiv.activity.satker.add

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.base.AmActivity
import com.ismealdi.dactiv.util.Constants.INTENT.SUCCESS
import com.ismealdi.dactiv.util.Dialogs
import com.ismealdi.dactiv.watcher.AmFourDigitWatcher
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_satker_add.*
import kotlinx.android.synthetic.main.toolbar_primary.*

/**
 * Created by Al on 15/10/2018
 */

class AddSatkerActivity : AmActivity(), AddSatkerContract.View {

    override lateinit var progress: KProgressHUD
    override lateinit var presenter: AddSatkerContract.Presenter

    fun init() {
        progress = Dialogs.initProgressDialog(this)
        presenter = AddSatkerPresenter(this, this)

        presenter.users()

        watcher()
        listener()

        setTitle(getString(R.string.title_add_satker))
        buttonBackToolbar.visibility = View.VISIBLE
        buttonMenuToolbar.visibility = View.VISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initData(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_satker_add)
        init()
    }

    override fun populateDialog(data: HashMap<String, String>) {
        layoutEselon.setDialogList(data)
        layoutKepala.setDialogList(data)
    }

    override fun onError(message: String) {
        showSnackBar(message)
    }

    override fun onSuccess(message: String) {
        showSnackBar(message, Snackbar.LENGTH_LONG)
    }

    override fun clearData() {
        textKepala.text = ""
        textEselon.text = ""
        textKodeKegiatan.setText("")
        textNamaSatker.setText("")
        textDeskripsi.setText("")

        setResult(SUCCESS)
        finish()
    }

    private fun listener() {
        buttonMenuToolbar.setOnClickListener {
            val kepalaId = if(textKepala.text.toString().isNotEmpty()) layoutKepala.getDialogSelected() else ""
            val eselons = if(textEselon.text.toString().isNotEmpty()) layoutEselon.getDialogSelected() else ""
            val nama = textNamaSatker.text.toString()
            val description = textDeskripsi.text.toString()
            val kodeKegiatan = textKodeKegiatan.text.toString().toNumber()

            presenter.validateInput(kepalaId, eselons, nama, description, kodeKegiatan)
        }

        buttonBackToolbar.setOnClickListener {
            onBackPressed()
        }
    }

    private fun watcher() {
        textKodeKegiatan.addTextChangedListener(AmFourDigitWatcher())
    }

    override fun onStop() {
        super.onStop()
        presenter.killSnapshot()
    }

}
