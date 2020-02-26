package com.balladie.soundtherapy.view.ui.splash

import com.balladie.soundtherapy.preference.AppPreference
import com.balladie.soundtherapy.view.ui.base.BaseViewModel
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val preference: AppPreference
) : BaseViewModel() {

    fun isFirstLogin(): Boolean = preference.isOldUser.not()
}