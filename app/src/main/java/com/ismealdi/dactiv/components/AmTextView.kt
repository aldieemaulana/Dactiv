package com.ismealdi.dactiv.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.view.View
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.util.Anims
import com.ismealdi.dactiv.util.Logs
import com.ismealdi.dactiv.util.Utils


/**
 * Created by Al on 06/10/2018
 */

class AmTextView : AppCompatTextView {

    private var mFont: String = "R"
    private var mPath: String = "fonts/Montserrat-"
    private var mType: String = ".ttf"
    private var mEditRes: Int = 0
    private var mLayoutRes: Int = 0
    private var mSelectedId: Int? = null

    private lateinit var mEdit: View
    private lateinit var mLayout: View

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setValues(attrs)
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setOnClickFocus()
    }

    fun setTextFade(text: String) {
        Anims().setTextFade(this, text)
    }

    fun setFont(font: String) {
        mFont = font
    }

    fun getFont(): String {
        return mFont
    }

    private fun init() {
        setFont(mFont)
        setNewTypeFace()
    }

    private fun setNewTypeFace() {
        val font = Typeface.createFromAsset(context.assets, mPath + mFont + mType)
        setTypeface(font, Typeface.NORMAL)
    }

    private fun setOnClickFocus() {
        if(mEditRes > 0 && mEditRes != -1) {
            mEdit = (parent as View).findViewById(mEditRes)!!

            setOnClickListener{

                if(mEdit.isEnabled) {
                    mEdit.requestFocus()
                    Utils.showKeyboard(context)
                }
            }
        }else if(mLayoutRes > 0 && mLayoutRes != -1) {
            mLayout = (parent as View).findViewById(mLayoutRes)!!

            setOnClickListener {
                mLayout.performClick()
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
                    R.styleable.AmView_AmFont -> mFont = attr.getString(attribute)
                    R.styleable.AmView_AmEdit -> mEditRes = attr.getResourceId(R.styleable.AmView_AmEdit, -1)
                    R.styleable.AmView_AmLayout -> mLayoutRes = attr.getResourceId(R.styleable.AmView_AmLayout, -1)
                    else -> Logs.d("Unknown attribute for " + javaClass.toString() + ": " + attribute)
                }
            }
        } finally {
            attr.recycle()
        }
    }

    fun setSelectedId(position: Int) {
        mSelectedId = position
    }

    fun getSelectedId() : Int? {
        return mSelectedId
    }


}