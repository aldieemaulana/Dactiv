package com.ismealdi.dactiv.activity.signin

import com.google.firebase.auth.FirebaseUser
import com.ismealdi.dactiv.base.AmPresenter
import com.ismealdi.dactiv.base.AmView


/**
 * Created by Al on 19/10/2018
 */

interface SignInContract {

    interface View : AmView<Presenter> {

        fun onSuccess(message: String)

        fun onError(message: String)

        fun onInfo(message: String)

        fun onAlert(message: String, actionText: String, actionListener: Runnable)

        fun showNextActivity()

    }

    interface Presenter : AmPresenter {

        fun validateInput(action: String, email: String, password: String)

        fun emailVerification(user: FirebaseUser)

        fun emailResetPassword(email: String)

        fun verifyEmail(user: FirebaseUser)

        fun register(email: String, password: String)

        fun login(email: String, password: String)

        fun subscribeMemberTopic(topic: String)

    }

}