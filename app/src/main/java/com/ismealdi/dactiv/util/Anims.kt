package com.ismealdi.dactiv.util

import android.animation.ObjectAnimator
import android.support.v7.widget.AppCompatTextView
import android.view.View

class Anims {

    internal fun setTextFade(textView: AppCompatTextView, title: String) {
        if(textView.text != title) {
            val fadeOutAnimation = ObjectAnimator.ofFloat(textView, View.ALPHA, 1.0f, 0.0f)
            val fadeInAnimation = ObjectAnimator.ofFloat(textView, View.ALPHA, 0.0f, 1.0f)

            fadeOutAnimation.duration = 500
            fadeOutAnimation.start()

            textView.text = title

            fadeInAnimation.duration = 500
            fadeInAnimation.start()
        }
    }

}
