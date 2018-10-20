package com.ismealdi.dactiv.activity.signin

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.ismealdi.dactiv.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Al on 20/10/2018
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
class SignInActivityTest {

    private var mUserEmail = "email"

    @Rule @JvmField var activityTestRule = ActivityTestRule(SignInActivity::class.java)

    @Before
    fun initValidString() {
        //mUserEmail = "Espresso"
    }

    @Test
    fun fillEmail() {
        onView(withId(R.id.textEmail)).perform(typeText(mUserEmail), closeSoftKeyboard())
        onView(withId(R.id.buttonSignIn)).perform(click())

        onView(withId(R.id.snackbar_text)).check(matches(isDisplayed()))
    }

}