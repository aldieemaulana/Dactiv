package com.ismealdi.dactiv.util

import android.support.design.widget.BaseTransientBottomBar
import android.view.View

/**
 * Created by Al on 14/10/2018
 */

internal class NoSwipeBehavior : BaseTransientBottomBar.Behavior() {
    override fun canSwipeDismissView(child: View):Boolean {
        return false
    }
}