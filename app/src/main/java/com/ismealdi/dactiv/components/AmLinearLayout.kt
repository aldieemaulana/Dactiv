package com.ismealdi.dactiv.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.util.Dialogs
import com.ismealdi.dactiv.util.Logs
import com.ismealdi.dactiv.util.Utils

/**
 * Created by Al on 26/06/2018
 */

class AmLinearLayout : android.support.v7.widget.LinearLayoutCompat {

    private var mEditRes: Int = 0
    private var mDialog: Boolean = false
    private var mDialogQuestion: String = ""
    private var mDialogList: List<String> = listOf()
    private var mKeysList: List<String> = listOf()

    private lateinit var mEdit: View

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setValues(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setValues(attrs)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setOnClickFocus()
    }

    fun setDialogList(list: HashMap<String, String>) {
        mDialogList = list.values.distinct()
        mKeysList = list.keys.distinct()
    }

    fun getDialogSelected() : String? {
        if(mKeysList.isNotEmpty()) {
            val mEditView = mEdit as AmTextView
            return mKeysList[mEditView.getSelectedId()!!]
        }

        return ""
    }

    fun getDialogList() : List<String> {
        return mDialogList
    }

    fun setDialogQuestion(question: String) {
        mDialogQuestion = question
    }

    fun setDialog(state: Boolean) {
        mDialog = state
    }

    fun getDialog() : Boolean {
        return mDialog
    }

    fun setEditText(editText: View) {
        mEdit = editText
    }

    private fun setOnClickFocus() {
        if(mEditRes > 0 && mEditRes != -1) {
            mEdit = (parent as View).findViewById(mEditRes)!!

            setOnClickListener{
                if(!mDialog) {
                    mEdit.requestFocus()
                    Utils.Keyboard.show(context)
                }else{
                    if(mDialogList.isNotEmpty()) {
                        Dialogs.spinnerDialog(context, mDialogList, mDialogQuestion, mEdit as AmTextView, false)
                    }
                }
            }

        }
    }

    fun init(list: HashMap<String, String>, state: Boolean, component: AmTextView, question: String) {
        setDialogList(list)
        setDialog(state)
        setEditText(component)
        setDialogQuestion(question)

        setOnClickListener {
            if (mDialogList.isNotEmpty()) {
                Dialogs.spinnerDialog(context, mDialogList, mDialogQuestion, mEdit as AmTextView, true)
            }
        }
    }

    @SuppressLint("CustomViewStyleable")
    private fun setValues(attrs: AttributeSet?) {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.AmView)
        try {
            val n = attr.indexCount
            for (i in 0 until n) {
                val attribute = attr.getIndex(i)
                when (attribute) {
                    R.styleable.AmView_AmEdit -> mEditRes = attr.getResourceId(R.styleable.AmView_AmEdit, -1)
                    R.styleable.AmView_AmDialog -> mDialog = attr.getBoolean(R.styleable.AmView_AmDialog, false)
                    else -> Logs.d("Unknown attribute for " + javaClass.toString() + ": " + attribute)
                }
            }
        } finally {
            attr.recycle()
        }
    }

}

