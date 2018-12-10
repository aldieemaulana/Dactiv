package com.ismealdi.dactiv.util

import android.util.Log
import com.ismealdi.dactiv.BuildConfig


/**
 * Created by Am
 */

object Logs {
    fun e(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.e("Am error", "AmMsg: $msg")
        }
    }

    fun d(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d("Am debug", "AmMsg: $msg")
        }
    }

    fun v(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.v("Am verbose","AmMsg: $msg")
        }
    }

    fun i(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.i("Am info","AmMsg: $msg")
        }
    }

    fun w(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.w("Am warn","AmMsg: $msg")
        }
    }

    fun log(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.e("Am","AmMsg: $msg")
        }
    }

    fun db(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.e("AmDatabase", msg)
        }
    }
}