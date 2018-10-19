package com.ismealdi.dactiv.base

import com.kaopiz.kprogresshud.KProgressHUD


/**
 * Created by Al on 19/10/2018
 */

interface AmView<T> {

    var presenter : T
    var progress : KProgressHUD

}
