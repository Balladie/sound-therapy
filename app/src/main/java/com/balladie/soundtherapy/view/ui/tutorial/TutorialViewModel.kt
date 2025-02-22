package com.balladie.soundtherapy.view.ui.tutorial

import com.balladie.soundtherapy.preference.AppPreference
import com.balladie.soundtherapy.view.ui.base.BaseViewModel
import javax.inject.Inject

class TutorialViewModel @Inject constructor(
    private val preference: AppPreference
) : BaseViewModel() {

    fun setLoginOn() {
        preference.setLoginOn()
    }

    fun getIsHealthAccessOn() = preference.gotHealthAccess

    fun getIsLocationAccessOn() = preference.gotLocationAccess

    fun turnOnHealthAccess() {
        preference.turnOnHealthAccess()
    }

    fun turnOnLocationAccess() {
        preference.turnOnLocationAccess()
    }
}