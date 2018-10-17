package com.ismealdi.dactiv.util

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.adapter.DialogListAdapter
import com.ismealdi.dactiv.components.AmTextView
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.dialog_spinner.view.*

object Dialogs {

    fun initProgressDialog(context: Context): KProgressHUD {
        return KProgressHUD.create(context)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(false)
            .setDimAmount(0.2f)
            .setCornerRadius(4f)
            .setSize(45,45)
            .setWindowColor(context.resources.getColor(R.color.colorWhite))
            .setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            .setAnimationSpeed(2)
    }

    @SuppressLint("InflateParams")
    fun spinnerDialog(context: Context, lists: List<String>, title: String, component: AmTextView, cancelledOnTouch: Boolean) {

        val dialog = Dialog(context, R.style.AppTheme_Dialog)
        val layoutInflater = LayoutInflater.from(context)
        val dialogView = layoutInflater.inflate(R.layout.dialog_spinner, null)
        val dialogListAdapter = DialogListAdapter(lists, context, dialog, component)

        if (title.isNotEmpty()) {
            dialogView.textTitle.text = title
            dialogView.textTitle.visibility = View.VISIBLE
        }

        if(component.text.toString().isNotEmpty()) {
            dialogListAdapter.selected(component.text.toString())
        }

        dialogView.listViewDialog.adapter = dialogListAdapter
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(dialogView)
        dialog.setCanceledOnTouchOutside(cancelledOnTouch)
        dialog.show()

    }

}
