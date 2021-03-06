package com.ismealdi.dactiv.activity.kegiatan

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import com.google.firebase.firestore.DocumentSnapshot
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.activity.kegiatan.AddKegiatanActivity.Companion.VALIDATE.INPUT_EMPTY
import com.ismealdi.dactiv.base.AmDraftActivity
import com.ismealdi.dactiv.model.Jabatan
import com.ismealdi.dactiv.model.Kegiatan
import com.ismealdi.dactiv.model.Satker
import com.ismealdi.dactiv.model.User
import com.ismealdi.dactiv.util.*
import com.ismealdi.dactiv.util.Constants.INTENT.DETAIL_SATKER
import com.ismealdi.dactiv.watcher.AmCurrencyWatcher
import com.ismealdi.dactiv.watcher.AmFourDigitWatcher
import kotlinx.android.synthetic.main.activity_kegiatan_add.*
import kotlinx.android.synthetic.main.toolbar_primary.*
import java.text.SimpleDateFormat
import java.util.*

class AddKegiatanActivity : AmDraftActivity() {

    companion object {
        object VALIDATE {
            const val  INPUT_EMPTY = "Please check your input data."
        }
    }

    private var mUsers : HashMap<String, User> = hashMapOf()
    private var mDataAdmin : HashMap<String, String> = hashMapOf()

    private var mSatkers : HashMap<String, Satker> = hashMapOf()
    private var mDataSatker : HashMap<String, String> = hashMapOf()

    private var mJabatans : HashMap<String, Jabatan> = hashMapOf()
    private var mDataJabatan : HashMap<String, String> = hashMapOf()

    private lateinit var datePicker: DatePickerDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        initData(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kegiatan_add)

        init()
    }

    private fun listener() {
        buttonBackToolbar.setOnClickListener {
            onBackPressed()
        }

        buttonMenuToolbar.setOnClickListener {
            validateInput()
        }

        layoutJadwalPelaksana.setOnClickListener {
            datePicker.show()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun validateInput() {
        val nama = textName.text.toString()
        val kodeKegiatan = textKodeKegiatan.text.toString()
        val anggaran = textAnggaran.text.toString()
        val jadwalPelaksana = textJadwalPelaksana.text
        val durasi = textDurasi.text.toString()
        val satkerId = textSatker.text.toString()
        val penanggungJawab = textPenanggung.getSelectedId()


        if (satkerId.isNotEmpty() && penanggungJawab != null &&
                kodeKegiatan.isNotEmpty() && anggaran.isNotEmpty() && jadwalPelaksana.isNotEmpty()
            && durasi.isNotEmpty() && nama.isNotEmpty()) {

            val data = Kegiatan()

            data.name = nama
            data.kodeKegiatan = kodeKegiatan.toNumber()
            data.durasi = durasi.toInt()
            data.anggaran = anggaran.toNumber().toLong()

            val formatter = SimpleDateFormat("dd/MM/yyyy h:m", Locale.ENGLISH)

            data.jadwal = formatter.parse(jadwalPelaksana.toString())
            data.penanggungJawab = mUsers[layoutPenanggung.getDialogSelected()]!!.uid
            data.bagian = mUsers[user?.uid]!!.bagian.toString()
            data.satker = if(intent.getParcelableExtra<Satker>(DETAIL_SATKER) != null) (intent.getParcelableExtra<Satker>(DETAIL_SATKER) as Satker).id else mSatkers[layoutSatker.getDialogSelected()]!!.id

            addKegiatan(data)

        }else {
            showSnackBar(layoutParent, INPUT_EMPTY, Snackbar.LENGTH_LONG, 100)
        }
    }

    private fun addKegiatan(data: Kegiatan) {
        val document = db!!.kegiatan().document()

        data.status = 1
        data.id = document.id
        data.admin = user?.uid.toString()

        document.set(data).addOnFailureListener {
            showSnackBar(layoutParent, it.message.toString(), Snackbar.LENGTH_LONG)
        }

        clearData()

        setResult(Constants.INTENT.SUCCESS)
        finish()
    }

    private fun clearData() {
        textSatker.text = ""
        textBagian.text = ""
        textPenanggung.text = ""
        textKodeKegiatan.setText("")
        textAnggaran.setText("")
        textJadwalPelaksana.text = ""
        textDurasi.setText("")
    }

    private fun init() {
        initCalendarDialog()
        watcher()
        listener()

        setTitle(getString(R.string.title_add_kegiatan))
        buttonBackToolbar.visibility = View.VISIBLE
        buttonMenuToolbar.visibility = View.VISIBLE

        initView()

        if(intent.getParcelableExtra<Satker>(DETAIL_SATKER) != null) {
            textSatker.text = (intent.getParcelableExtra<Satker>(DETAIL_SATKER)).name
            layoutSatker.isClickable = false
            layoutSatker.isEnabled = false
            textSatker.isClickable = false
            textSatker.isEnabled = false
        }
    }

    private fun watcher() {
        textKodeKegiatan.addTextChangedListener(AmFourDigitWatcher())
        textAnggaran.addTextChangedListener(AmCurrencyWatcher(textAnggaran))
    }

    private fun initView() {
        getUsers()
        getJabatan()
        getSatker()
    }

    private fun getUsers() {
        showProgress()
        db!!.user().addSnapshotListener { it, e ->
            if(e != null) {
                showSnackBar(layoutParent, e.message.toString(), Snackbar.LENGTH_LONG)
            }

            if (it != null && it.documents.isNotEmpty()) {
                it.documents.forEach { document: DocumentSnapshot? ->
                    if(document != null && document.exists()) {
                        val mUser = document.toObject(User::class.java)
                        if(mUser != null) {
                            mDataAdmin[mUser.uid] = mUser.displayName
                            mUsers[mUser.uid] = mUser
                        }
                    }
                }

                if(mDataAdmin.isNotEmpty()) {
                    layoutPenanggung.setDialogList(mDataAdmin)
                }

            }

            hideProgress()
        }
    }

    private fun getJabatan() {
        showProgress()
        db!!.jabatan().addSnapshotListener { it, e ->
            if(e != null) {
                showSnackBar(layoutParent, e.message.toString(), Snackbar.LENGTH_LONG)
            }

            if (it != null && it.documents.isNotEmpty()) {
                it.documents.forEach { document: DocumentSnapshot? ->
                    if(document != null && document.exists()) {
                        val mJabatan = document.toObject(Jabatan::class.java)
                        if(mJabatan != null && mJabatan.nama != "-") {
                            mDataJabatan[document.id] = mJabatan.nama
                            mJabatans[document.id] = mJabatan
                        }
                    }
                }

                if(mDataJabatan.isNotEmpty()) {
                    layoutBagian.setDialogList(mDataJabatan)
                }

            }

            hideProgress()
        }
    }

    private fun getSatker() {
        showProgress()
        db!!.satker().orderBy(satkerFields.createdOn).addSnapshotListener { it, e ->
            if(e != null) {
                showSnackBar(layoutParent, e.message.toString(), Snackbar.LENGTH_LONG)
            }

            if (it != null && it.documents.isNotEmpty()) {
                it.documents.forEach { document: DocumentSnapshot? ->
                    if(document != null && document.exists()) {
                        val mSatker = document.toObject(Satker::class.java)
                        if(mSatker != null) {
                            mDataSatker[mSatker.id] = mSatker.name
                            mSatkers[mSatker.id] =  mSatker
                        }
                    }
                }

                if(mDataSatker.isNotEmpty()) {
                    layoutSatker.setDialogList(mDataSatker)
                }

            }

            hideProgress()
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

        val timePickerDialog = TimePickerDialog(context,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    textJadwalPelaksana.text = textJadwalPelaksana.text.toString() + " $hourOfDay:$minute"
                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true)

        timePickerDialog.setOnCancelListener {
            textJadwalPelaksana.text =  "$day/${month+1}/$year ${c.get(Calendar.HOUR_OF_DAY)}:${c.get(Calendar.MINUTE)}"
        }

        datePicker = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { _, y, m, d ->
            val mm = m + 1
            textJadwalPelaksana.text =  "$d/$mm/$y"
            timePickerDialog.show()
        }, year, month, day)

        datePicker.datePicker.minDate = System.currentTimeMillis() - 1000

        datePicker.setOnCancelListener {
            textJadwalPelaksana.text = ""
        }
    }

}
