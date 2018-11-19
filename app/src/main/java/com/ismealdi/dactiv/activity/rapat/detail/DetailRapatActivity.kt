package com.ismealdi.dactiv.activity.rapat.detail

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.format.DateFormat
import android.view.View
import android.widget.LinearLayout
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.vision.barcode.Barcode
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.ismealdi.dactiv.App
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.adapter.UserAdapter
import com.ismealdi.dactiv.base.AmActivity
import com.ismealdi.dactiv.model.Attendent
import com.ismealdi.dactiv.model.Rapat
import com.ismealdi.dactiv.model.User
import com.ismealdi.dactiv.util.BarcodeEncoder
import com.ismealdi.dactiv.util.Constants
import com.ismealdi.dactiv.util.Constants.INTENT.DETAIL_RAPAT
import com.ismealdi.dactiv.util.Dialogs
import com.ismealdi.dactiv.util.barcode.BarcodeCaptureActivity
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_rapat_detail.*
import kotlinx.android.synthetic.main.toolbar_primary.*
import net.glxn.qrgen.android.QRCode
import java.text.SimpleDateFormat
import java.util.*

class DetailRapatActivity : AmActivity(), DetailRapatContract.View {

    override lateinit var progress: KProgressHUD
    override lateinit var presenter: DetailRapatContract.Presenter

    override var mUsers : MutableList<User> = mutableListOf()
    override var mAttendents : MutableList<Attendent> = mutableListOf()

    private lateinit var mAdapter : UserAdapter
    private lateinit var mRapat : Rapat
    private lateinit var datePicker: DatePickerDialog

    private val request = 20
    private val capture = 9001

    fun init() {

        mRapat = intent.getParcelableExtra(DETAIL_RAPAT)

        progress = Dialogs.initProgressDialog(this)
        presenter = DetailRapatPresenter(this, this)

        presenter.users()
        presenter.attendents(mRapat)

        initList()
        listener()

        setTitle(getString(R.string.title_rapat))
        buttonBackToolbar.visibility = View.VISIBLE
        buttonMenuToolbar.visibility = View.GONE
        buttonMenuToolbar.setPadding(10,10,10,10)
        buttonMenuToolbar.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_qr_code))

        setData(mRapat)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initData(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rapat_detail)
        init()
    }

    private fun initList() {
        mAdapter = UserAdapter(mutableListOf(), mutableListOf())
        recyclerView.layoutManager = LinearLayoutManager(applicationContext,
                LinearLayout.VERTICAL, false)
        recyclerView.adapter = mAdapter
        showEmpty(true)

    }

    private fun listener() {
        buttonBackToolbar.setOnClickListener {
            onBackPressed()
        }

        buttonMenuToolbar.setOnClickListener {
            doScanBarcode()
        }

        layoutJadwalPelaksana.setOnClickListener {
            initCalendarDialog()
            datePicker.show()
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

    override fun populateAttendent(users: MutableList<User>) {
        mAdapter.updateUser(users)
        checkState()
    }

    private fun checkState() {
        if(mRapat.status > 2 || mRapat.attendent.any { x -> x.user == App.fireBaseAuth.currentUser!!.uid }) {
            buttonMenuToolbar.visibility = View.GONE
            layoutAdmin.visibility = View.GONE
        }else{
            buttonMenuToolbar.visibility = View.VISIBLE
        }
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

    override fun onError(message: String) {
        showSnackBar(message)
    }

    override fun onDoneAttend() {
        checkState()
    }

    override fun onDoneRapat() {
        checkState()
        textDeskripsi.setText("")
        textJadwalPelaksana.text = ""
    }

    @SuppressLint("SetTextI18n")
    override fun displayPenanggunJawab(user: User) {
        if(App.fireBaseAuth.currentUser != null) {
            buttonMessage.isEnabled = true
            textPenanggung.text = getString(R.string.text_admin) + ": ${user.displayName}"
            if (App.fireBaseAuth.currentUser!!.uid != user.uid) {
                buttonMessage.setOnClickListener {
                    //openMessage(user)
                }
            }else{
                if(mRapat.status == 1 || mRapat.status == 2) {
                    buttonMenuToolbar.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_checked))
                    buttonMenuToolbar.visibility = View.VISIBLE
                    buttonMenuToolbar.isEnabled = true
                    buttonMenuToolbar.setPadding(18, 18, 18, 18)

                    buttonMenuToolbar.setOnClickListener {
                        presenter.setAsDone(mRapat, textJadwalPelaksana.text.toString(), textDeskripsi.text.toString(), alasan = textAlasan.text.toString())
                    }
                }

                buttonMessage.setOnClickListener {
                    //remindAll(mRapat.id, user)
                }
            }
        }
    }

    override fun reloadAttendent(mAttendents: MutableList<Attendent>) {
        this.mAttendents = mAttendents
        mAdapter.updateData(mAttendents)
        showEmpty((mAttendents.size == 0), Constants.SHARED.defaultDelay)

    }

    @SuppressLint("SetTextI18n")
    override fun setData(rapat: Rapat) {
        textName.text = rapat.agendaRapat
        textDate.text = DateFormat.format("d MMMM yyyy hh:mm", rapat.tanggalRapat).toString()
        buttonAlarm.isEnabled = false
        layoutAdmin.visibility = View.GONE

        val multiFormatWriter = MultiFormatWriter()

        try {
            val bitMatrix = multiFormatWriter.encode(rapat.code.toString(), BarcodeFormat.CODE_39,1000,450)
            val barcodeEncoder = BarcodeEncoder()
            imageBarCode.setImageBitmap(barcodeEncoder.createBitmap(bitMatrix))
        } catch (e: WriterException) {
            e.printStackTrace()
        }

        if(App.fireBaseAuth.currentUser != null) {

            checkState()

            if (rapat.admin == App.fireBaseAuth.currentUser!!.uid && DateFormat.format("d MMMM yyyy", rapat.tanggalRapat) == DateFormat.format("d MMMM yyyy", Calendar.getInstance())) {
                imageOverlay.visibility = View.GONE
                if(rapat.status == 1 || rapat.status == 2) {
                    layoutAdmin.visibility = View.VISIBLE
                    layoutAlasan.visibility = View.GONE


                    if(rapat.status == 2) {
                        layoutAlasan.isEnabled = false
                        textDeskripsiAlasan.isEnabled = false
                        labelTanggal.text = getString(R.string.text_tanggal_tindak_lanjut)
                        labelDeskripsi.text = getString(R.string.text_deskripsi)
                        textDeskripsi.hint = getString(R.string.text_deskripsi_hint)
                        layoutAlasan.visibility = View.VISIBLE
                    }else if(rapat.status == 1) {
                        labelTanggal.text = getString(R.string.text_tanggal_batas_tindak_lanjut)
                        labelDeskripsi.text = getString(R.string.text_no_tulen)
                        textDeskripsi.hint = getString(R.string.text_no_tulen_hint)
                    }
                }
            }

            if(rapat.status > 1){
                textAlasan.visibility = View.VISIBLE
                textAlasan.text = rapat.notulenRapat

                textDateBatas.visibility = View.VISIBLE
                textDateBatas.text = DateFormat.format("d MMMM yyyy", rapat.tanggalBatasTindakLanjut).toString()
            }

            textDate.text = DateFormat.format("d MMMM yyyy hh:mm", rapat.tanggalRapat).toString()

            presenter.penanggungJawab(rapat.admin)
            mRapat = rapat
        }

    }

    private fun doScanBarcode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                callBarcode()
            }else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission_group.CAMERA, Manifest.permission.CAMERA), request)
            }
        }else{
            callBarcode()
        }
    }

    private fun callBarcode() {
        val intent = Intent(this, BarcodeCaptureActivity::class.java)
        intent.putExtra(BarcodeCaptureActivity.AutoFocus, true)
        startActivityForResult(intent, capture)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == request) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                callBarcode()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == capture) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    (data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject) as Barcode).let {
                        presenter.doAttend(it.displayValue, mRapat)
                    }
                }
            } else {
                showSnackBar("Invalid error!")
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initCalendarDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = intent.getIntExtra(Constants.INTENT.SELECTED_DATE, c.get(Calendar.DAY_OF_MONTH))

        if(intent.getIntExtra(Constants.INTENT.SELECTED_DATE, 0) > 0) {
            textJadwalPelaksana.text = "$day/${month + 1}/$year ${c.get(Calendar.HOUR_OF_DAY)}:${c.get(Calendar.MINUTE)}"
        }

        val timePickerDialog = TimePickerDialog(this,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    textJadwalPelaksana.text = textJadwalPelaksana.text.toString() + " $hourOfDay:$minute"

                    if(mRapat.status == 2) {
                        val jad = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(textJadwalPelaksana.text.toString())
                        val dat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(DateFormat.format("dd/MM/yyyy", mRapat.tanggalBatasTindakLanjut).toString())

                        if(dat.time < jad.time) {
                            layoutAlasan.isEnabled = true
                            textDeskripsiAlasan.isEnabled = true
                        }else{
                            layoutAlasan.isEnabled = false
                            textDeskripsiAlasan.isEnabled = false
                        }
                    }

                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true)

        timePickerDialog.setOnCancelListener {
            textJadwalPelaksana.text = ""
        }

        datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, y, m, d ->
            val mm = m + 1
            textJadwalPelaksana.text =  "$d/$mm/$y"
            timePickerDialog.show()
        }, year, month, day)

        datePicker.datePicker.minDate = System.currentTimeMillis() - 1000

        datePicker.setOnCancelListener {
            textJadwalPelaksana.text = ""
            textDeskripsi.setText("")
        }
    }
}
