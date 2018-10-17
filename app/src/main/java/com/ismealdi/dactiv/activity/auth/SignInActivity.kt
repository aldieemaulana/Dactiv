package com.ismealdi.dactiv.activity.auth

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.view.View
import android.view.inputmethod.EditorInfo
import com.google.firebase.auth.FirebaseUser
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.activity.MainActivity
import com.ismealdi.dactiv.activity.auth.SignInActivity.Companion.INTERVAL.RESET_ATTEMPT
import com.ismealdi.dactiv.activity.auth.SignInActivity.Companion.VALIDATE.EMAIL_PASSWORD_EMPTY
import com.ismealdi.dactiv.base.AmActivity
import com.ismealdi.dactiv.listener.User.addFromRegister
import com.ismealdi.dactiv.listener.User.verifiedUser
import com.ismealdi.dactiv.util.Constants
import com.ismealdi.dactiv.util.Logs
import com.ismealdi.dactiv.util.RevealAnimation
import com.ismealdi.dactiv.util.Utils
import kotlinx.android.synthetic.main.activity_sign_in.*


/**
 * Created by Al on 14/10/2018
 */

class SignInActivity : AmActivity() {

    private var mLoginFailed = 0

    companion object {
        object VALIDATE {
            const val  EMAIL_PASSWORD_EMPTY = "Please check your email address and password input."
        }

        object INTERVAL {
            const val RESET_ATTEMPT = 30000
        }
    }

    private fun listener() {
        buttonSignIn.setOnClickListener {
            validateInput(true)
        }

        buttonSignUp.setOnClickListener {
            validateInput()
        }

        textPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Utils.hideKeyboard(this)
                buttonSignIn.performClick()

            }

            true
        }
    }

    private fun validateInput(isLogin: Boolean = false) {
        val email = textEmail.text.toString()
        val password = textPassword.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty())
            if(isLogin) onLogin(email,password) else onRegister(email, password)
        else
            showSnackBar(layoutParent, EMAIL_PASSWORD_EMPTY, Snackbar.LENGTH_LONG, 500)
    }

    private fun sendEmailVerification(user: FirebaseUser?) {
        user!!.sendEmailVerification().addOnCompleteListener {
            if (it.isSuccessful) {
                showSnackBar(layoutParent, "Verification email sent to " + user.email, Snackbar.LENGTH_INDEFINITE, 500, "Verify", Runnable { verifyEmail() })
            } else {
                showSnackBar(layoutParent, "Failed to send verification email.")
            }
        }
    }

    private fun verifyEmail() {
        val user = auth?.currentUser

        user?.reload()

        if(user!!.isEmailVerified) {
            verifiedUser(user.uid, true)
            startAnActivity(layoutParent)
        } else
            showSnackBar(layoutParent, "Failed your account is unverified " + user.email, Snackbar.LENGTH_INDEFINITE, 500, "Verify", Runnable { verifyEmail() })
    }

    private fun onRegister(email: String, password: String) {
        showProgress()

        auth?.createUserWithEmailAndPassword(email, password)!!.addOnCompleteListener {
            if (it.isSuccessful) {
                addFromRegister(auth?.currentUser)
                sendEmailVerification(auth?.currentUser)
            } else {
                Logs.e("createUserWithEmail:failure:" + it.exception?.message.toString())
                showSnackBar(layoutParent, it.exception?.message.toString())
            }
        }
    }

    private fun onLogin(email: String, password: String) {
        showProgress()

        if(mLoginFailed < 3) {
            auth?.signInWithEmailAndPassword(email, password)!!.addOnCompleteListener {
                if (it.isSuccessful) {
                    hideProgress()
                    startAnActivity(layoutParent)
                } else {
                    val message = it.exception?.message.toString()

                    if(message.contains("password is invalid"))
                        mLoginFailed++

                    Logs.e("signInUserWithEmail:failure:" + it.exception?.message.toString())

                    if(message.contains("Too many unsuccessful login attempts")) {
                        mLoginFailed = 3
                        showSnackBar(layoutParent, "Too many attempts, try again later or reset password", Snackbar.LENGTH_INDEFINITE, 500, "Reset", Runnable { resetPassword(email) })
                    }else
                        showSnackBar(layoutParent, message)
                }
            }
        }else{
            Handler().postDelayed({ mLoginFailed = 0 }, RESET_ATTEMPT.toLong())
            showSnackBar(layoutParent, "Too many attempts, try again later or reset password", Snackbar.LENGTH_INDEFINITE, 500, "Reset", Runnable { resetPassword(email) })
        }
    }

    private fun resetPassword(email: String) {
        showProgress()

        auth?.sendPasswordResetEmail(email)!!.addOnCompleteListener {
            if (it.isSuccessful) {
                showSnackBar(layoutParent, "Reset password link sent to $email, Please login after sometimes.", Snackbar.LENGTH_LONG, 250)
            }else{
                Logs.e("createUserWithEmail:failure:" + it.exception?.message.toString())
                showSnackBar(layoutParent, it.exception?.message.toString())
            }
        }
    }

    private fun init() {
        RevealAnimation(layoutParent, intent, context as Activity)

        listener()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initData(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        init()
    }

    private fun startAnActivity(v: View) {
        registerFireBase()

        val bounds = Rect()
        v.getDrawingRect(bounds)

        val intent = Intent(context, MainActivity::class.java)

        intent.putExtra(Constants.INTENT.LOGIN.FIRST_LOGIN, true)
        intent.putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_X, bounds.centerX())
        intent.putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_Y, bounds.centerY())

        ActivityCompat.startActivity(context, intent, null)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    private fun registerFireBase() {
        if(msg != null) {
            msg!!.subscribeToTopic(getString(R.string.default_channel))
        }
    }


}