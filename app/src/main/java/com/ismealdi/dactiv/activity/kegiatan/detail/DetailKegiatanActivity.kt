package com.ismealdi.dactiv.activity.kegiatan.detail

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.base.AmActivity
import com.ismealdi.dactiv.model.Kegiatan
import com.ismealdi.dactiv.util.Constants.INTENT.DETAIL_KEGIATAN
import com.ismealdi.dactiv.util.Dialogs
import com.ismealdi.dactiv.util.Utils
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_kegiatan_detail.*
import kotlinx.android.synthetic.main.toolbar_primary.*
import android.text.format.DateFormat
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.ismealdi.dactiv.util.barcode.BarcodeCaptureActivity
import java.text.NumberFormat
import java.util.*


/**
 * Created by Al on 23/10/2018
 */
class DetailKegiatanActivity : AmActivity(), DetailKegiatanContract.View {

    internal lateinit var mKegiatan : Kegiatan

    override lateinit var presenter: DetailKegiatanContract.Presenter
    override lateinit var progress: KProgressHUD

    private var format: NumberFormat? = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    private val request = 20
    private var detector: BarcodeDetector? = null
    private val capture = 9001


    fun init() {
        mKegiatan = intent.getParcelableExtra(DETAIL_KEGIATAN)

        progress = Dialogs.initProgressDialog(this)
        presenter = DetailKegiatanPresenter(this, this)

        listener()

        setTitle(getString(R.string.title_kegiatan))
        buttonBackToolbar.visibility = View.VISIBLE
        buttonMenuToolbar.visibility = View.VISIBLE
        buttonMenuToolbar.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_plus))

        setData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initData(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kegiatan_detail)
        init()
    }

    private fun listener() {
        buttonBackToolbar.setOnClickListener {
            onBackPressed()
        }

        buttonMenuToolbar.setOnClickListener {
            doScanBarcode()
        }
    }

    private fun setData() {
        textName.setTextFade(mKegiatan.name)
        textKodeKegiatan.setTextFade(Utils.stringKodeFormat(mKegiatan.kodeKegiatan))
        textAnggaran.setTextFade(format!!.format(mKegiatan.anggaran))
        textDate.setTextFade(DateFormat.format("d MMMM yyyy hh:mm", mKegiatan.createdOn.toDate()).toString())
    }

    private fun doScanBarcode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            ActivityCompat.requestPermissions(this@DetailKegiatanActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission_group.CAMERA, Manifest.permission.CAMERA), request)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            request -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(this, BarcodeCaptureActivity::class.java)
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true)
                startActivityForResult(intent, capture)
            } else {
                showSnackBar("Camera Permission Denied!")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == capture) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    val barcode: Barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject)
                    //textSurveyCode.setText(barcode.displayValue)
                }
            } else {
                showSnackBar("Invalid error!")
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}