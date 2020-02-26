package com.balladie.soundtherapy.view.ui.main

import com.balladie.soundtherapy.preference.AppPreference
import com.balladie.soundtherapy.view.ui.base.BaseViewModel
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val preference: AppPreference
) : BaseViewModel() {

    fun getLastPageIdx() = preference.lastPageIdx

    fun saveLastPageIdx(idx: Int) {
        preference.setLastPageIdx(idx)
    }

    fun getSavedPauseInfo() = preference.wasPaused

    fun savePauseInfo(paused: Boolean) {
        preference.setWasPaused(paused)
    }
}