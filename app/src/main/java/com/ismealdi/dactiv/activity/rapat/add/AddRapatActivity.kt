package com.ismealdi.dactiv.activity.rapat.add

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.base.AmActivity
import com.ismealdi.dactiv.util.Constants
import com.ismealdi.dactiv.util.Constants.INTENT.SUCCESS
import com.ismealdi.dactiv.util.Dialogs
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_rapat_add.*
import kotlinx.android.synthetic.main.toolbar_primary.*
import java.util.*

class AddRapatActivity : AmActivity(), AddRapatContract.View {


    override lateinit var progress: KProgressHUD
    override lateinit var presenter: AddRapatContract.Presenter

    private lateinit var datePicker: DatePickerDialog

    fun init() {
        progress = Dialogs.initProgressDialog(this)
        presenter = AddRapatPresenter(this, this)

        presenter.users()

        listener()

        setTitle(getString(R.string.title_add_rapat))
        buttonBackToolbar.visibility = View.VISIBLE
        buttonMenuToolbar.visibility = View.VISIBLE

        initCalendarDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initData(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rapat_add)
        init()
    }

    override fun onSuccess(message: String) {
        showSnackBar(message, Snackbar.LENGTH_LONG)
    }

    override fun onError(message: String) {
        showSnackBar(message)
    }

    override fun clearData() {
        textAgendaRapat.setText("")
        textJadwalPelaksana.text = ""
        textAdmin.text = ""

        setResult(SUCCESS)
        finish()
    }

    override fun populateDialog(data: HashMap<String, String>) {
        layoutAdmin.setDialogList(data)
    }

    private fun listener() {
        buttonMenuToolbar.setOnClickListener {
            val agendaRapat = textAgendaRapat.text.toString()
            val tanggalRapat = textJadwalPelaksana.text.toString()
            val adminId = if(textAdmin.text.toString().isNotEmpty()) layoutAdmin.getDialogSelected() else ""

            presenter.validateInput(agendaRapat, tanggalRapat, adminId)
        }

        buttonBackToolbar.setOnClickListener {
            onBackPressed()
        }

        layoutTanggalRapat.setOnClickListener {
            datePicker.show()
        }
    }

    override fun onStop() {
        super.onStop()
        presenter.killSnapshot()
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
                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true)

        timePickerDialog.setOnCancelListener {
            textJadwalPelaksana.text =  "$day/${month+1}/$year ${c.get(Calendar.HOUR_OF_DAY)}:${c.get(Calendar.MINUTE)}"
        }

        datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, y, m, d ->
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
