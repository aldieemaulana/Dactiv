package com.ismealdi.dactiv.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator


/**
 * Created by Al on 11/10/2018
 */

class RevealAnimation(private val mView: View, intent: Intent, private val mActivity: Activity) {

    private var revealX: Int = 0
    private var revealY: Int = 0

    init {

        //when you're android version is at leat Lollipop it starts the reveal activity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                intent.hasExtra(Companion.EXTRA_CIRCULAR_REVEAL_X) &&
                intent.hasExtra(Companion.EXTRA_CIRCULAR_REVEAL_Y)) {
            mView.visibility = View.INVISIBLE

            revealX = intent.getIntExtra(Companion.EXTRA_CIRCULAR_REVEAL_X, 0)
            revealY = intent.getIntExtra(Companion.EXTRA_CIRCULAR_REVEAL_Y, 0)

            val viewTreeObserver = mView.viewTreeObserver
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        revealActivity(revealX, revealY)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }
                    }
                })
            }
        } else {

            //if you are below android 5 it jist shows the activity
            mView.visibility = View.VISIBLE
        }
    }

    fun revealActivity(x: Int, y: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val finalRadius = (Math.max(mView.width, mView.height) * 1.1).toFloat()

            // create the animator for this view (the start radius is zero)
            val circularReveal = ViewAnimationUtils.createCircularReveal(mView, x, y, 0f, finalRadius)
            circularReveal.duration = 1000
            circularReveal.interpolator = AccelerateInterpolator()

            // make the view visible and start the animation
            mView.visibility = View.VISIBLE
            circularReveal.start()
        } else {
            mActivity.finish()
        }
    }

    fun unRevealActivity(intent: Intent, context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ActivityCompat.startActivity(context, intent, null)
            mActivity.finish()
            mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } else {
            val finalRadius = (Math.max(mView.width, mView.height) * 1.1)
            val circularReveal = ViewAnimationUtils.createCircularReveal(
                    mView, revealX, revealY, finalRadius.toFloat(), 0f)

            circularReveal.duration = 1000
            circularReveal.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    mView.visibility = View.INVISIBLE

                    ActivityCompat.startActivity(context, intent, null)

                    mActivity.finish()
                    mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
            })

            circularReveal.start()
        }
    }

    companion object {
        const val EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X"
        const val EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y"
    }
}