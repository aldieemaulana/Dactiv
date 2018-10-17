package com.ismealdi.dactiv.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.ArrayAdapter
import com.ismealdi.dactiv.R


/**
 * Created by Al on 16/10/2018
 */

class AmSpinner : android.support.v7.widget.AppCompatSpinner {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    @SuppressLint("CustomViewStyleable", "PrivateResource")
    private fun init(attrs: AttributeSet?) {
        val attr = context.obtainStyledAttributes(attrs, android.support.v7.appcompat.R.styleable.Spinner)
        try {
            val entries = attr.getTextArray(android.support.v7.appcompat.R.styleable.Spinner_android_entries)

            if (entries != null) {
                val adapter = ArrayAdapter<CharSequence>(
                        context, R.layout.am_spinner_item, entries)
                adapter.setDropDownViewResource(R.layout.am_spinner_dropdown_item)
                setAdapter(adapter)
            }
        } finally {
            attr.recycle()
        }

    }

}