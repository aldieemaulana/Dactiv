package com.ismealdi.dactiv.activity.signin

import android.content.Context
import com.google.firebase.auth.FirebaseUser
import com.ismealdi.dactiv.App
import com.ismealdi.dactiv.activity.signin.SignInActivity.Companion.ACTION.RESET_PASSWORD
import com.ismealdi.dactiv.activity.signin.SignInActivity.Companion.ACTION.SIGN_IN
import com.ismealdi.dactiv.activity.signin.SignInActivity.Companion.ACTION.SIGN_UP
import com.ismealdi.dactiv.activity.signin.SignInPresenter.Companion.INFO.EMAIL_RESENT_PASSWORD_SENT
import com.ismealdi.dactiv.activity.signin.SignInPresenter.Companion.INFO.EMAIL_VERIFICATION_FAIL
import com.ismealdi.dactiv.activity.signin.SignInPresenter.Companion.INFO.EMAIL_VERIFICATION_SENT
import com.ismealdi.dactiv.activity.signin.SignInPresenter.Companion.VALIDATE.EMAIL_EMPTY_RESET
import com.ismealdi.dactiv.activity.signin.SignInPresenter.Companion.VALIDATE.EMAIL_PASSWORD_EMPTY
import com.ismealdi.dactiv.activity.signin.SignInPresenter.Companion.VALIDATE.EMAIL_PASSWORD_EMPTY_REGISTER
import com.ismealdi.dactiv.services.AmMessagingService
import com.ismealdi.dactiv.structure.User.addFromRegister
import com.ismealdi.dactiv.structure.User.verifiedUser
import com.ismealdi.dactiv.util.Constants.STRING.ACTION.VERIFY
import com.ismealdi.dactiv.util.Preferences


/**
 * Created by Al on 19/10/2018
 */

class SignInPresenter(private val view: SignInContract.View, val context: Context) : SignInContract.Presenter {

    private var auth = App.fireBaseAuth
    private var message = App.fireBaseMsg

    init {
        view.presenter = this
    }

    override fun validateInput(action: String, email: String, password: String) {
        view.progress.show()

        when(action) {
            SIGN_IN -> {
                if(email.isNotEmpty() && password.isNotEmpty()) {
                    login(email, password)
                }else{
                    view.progress.dismiss()
                    view.onError(EMAIL_PASSWORD_EMPTY)
                }
            }

            SIGN_UP -> {
                if(email.isNotEmpty() && password.isNotEmpty()) {
                    register(email, password)
                }else{
                    view.progress.dismiss()
                    view.onError(EMAIL_PASSWORD_EMPTY_REGISTER)
                }
            }

            RESET_PASSWORD -> {
                if(email.isNotEmpty()) {
                    emailResetPassword(email)
                }else{
                    view.progress.dismiss()
                    view.onError(EMAIL_EMPTY_RESET)
                }
            }
        }
    }

    override fun emailVerification(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener {
            view.progress.dismiss()
            if (it.isSuccessful) {
                view.onAlert(EMAIL_VERIFICATION_SENT + user.email, VERIFY, Runnable { verifyEmail(user) })
            } else {
                view.onError(it.exception?.message.toString())
            }
        }
    }

    override fun emailResetPassword(email: String) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                view.progress.dismiss()
                view.onSuccess(EMAIL_RESENT_PASSWORD_SENT + email)
            }else{
                view.progress.dismiss()
                view.onError(it.exception?.message.toString())
            }
        }
    }

    override fun verifyEmail(user: FirebaseUser) {
        user.reload()

        if(user.isEmailVerified) {
            verifiedUser(user.uid)
            view.showNextActivity()
        } else
            view.onAlert(EMAIL_VERIFICATION_FAIL + user.email, VERIFY, Runnable { verifyEmail(user) })
    }

    override fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val user = auth.currentUser
                if(user != null) {
                    addFromRegister(user)
                    emailVerification(user)
                }else{
                    view.progress.dismiss()
                }
            } else {
                view.progress.dismiss()
                view.onError(it.exception?.message.toString())
            }
        }
    }

    override fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            view.progress.dismiss()

            if (it.isSuccessful)
                view.showNextActivity()
            else
                view.onError(it.exception?.message.toString())
        }

    }

    override fun subscribeMemberTopic(topic: String) {
        AmMessagingService().storeOnline(true)
        AmMessagingService().storeToken(Preferences(context).getToken())
        message.subscribeToTopic(topic)
    }

    companion object {
        object VALIDATE {
            const val EMAIL_EMPTY_RESET = "Please put your email address to reset the password"
            const val EMAIL_PASSWORD_EMPTY = "Please check your email address and password input."
            const val EMAIL_PASSWORD_EMPTY_REGISTER = "Put your email address and password on form above"
        }

        object ERROR {
            const val TOO_MANY_ATTEMP  = "Too many attempts, try again later or reset password"
        }

        object INFO {
            const val EMAIL_VERIFICATION_SENT  = "Verification email sent to "
            const val EMAIL_VERIFICATION_FAIL  = "Failed your account is unverified "
            const val EMAIL_RESENT_PASSWORD_SENT = "Reset password link sent to "
        }
    }

}