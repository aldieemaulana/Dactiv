package com.ismealdi.dactiv.activity.auth.signin

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.view.inputmethod.EditorInfo
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.activity.MainActivity
import com.ismealdi.dactiv.base.AmActivity
import com.ismealdi.dactiv.util.Constants.INTENT.LOGIN.FIRST_LOGIN
import com.ismealdi.dactiv.util.Dialogs
import com.ismealdi.dactiv.util.RevealAnimation
import com.ismealdi.dactiv.util.RevealAnimation.Companion.EXTRA_CIRCULAR_REVEAL_X
import com.ismealdi.dactiv.util.RevealAnimation.Companion.EXTRA_CIRCULAR_REVEAL_Y
import com.ismealdi.dactiv.util.Utils
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_sign_in.*


/**
 * Created by Al on 14/10/2018
 */

class SignInActivity : AmActivity(), SignInContract.View {

    override lateinit var progress: KProgressHUD
    override lateinit var presenter: SignInContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        progress = Dialogs.initProgressDialog(this)
        RevealAnimation(layoutParent, intent, this)
        presenter = SignInPresenter(this, this)

        listener()
    }

    override fun onAlert(message: String, actionText: String, actionListener: Runnable) {
        showSnackBar(message, Snackbar.LENGTH_INDEFINITE, actionText, actionListener)
    }

    override fun onError(message: String) {
        showSnackBar(message)
    }

    override fun onSuccess(message: String) {
        showSnackBar(message, Snackbar.LENGTH_LONG)
    }

    override fun onInfo(message: String) {
        showSnackBar(message)
    }


    override fun showNextActivity() {
        presenter.subscribeMemberTopic(getString(R.string.default_channel))

        val intent = Intent(applicationContext, MainActivity::class.java)
        val bounds = Rect()

        layoutParent.getDrawingRect(bounds)

        intent.putExtra(FIRST_LOGIN, true)
        intent.putExtra(EXTRA_CIRCULAR_REVEAL_X, bounds.centerX())
        intent.putExtra(EXTRA_CIRCULAR_REVEAL_Y, bounds.centerY())

        ActivityCompat.startActivity(applicationContext, intent, null)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    fun listener() {

        buttonSignIn.setOnClickListener {
            presenter.validateInput(true, textEmail.text.toString(), textPassword.text.toString())
        }

        buttonSignUp.setOnClickListener {
            presenter.validateInput(false, textEmail.text.toString(), textPassword.text.toString())
        }

        textPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Utils.hideKeyboard(this)
                buttonSignIn.performClick()
            }

            true
        }
    }

}
