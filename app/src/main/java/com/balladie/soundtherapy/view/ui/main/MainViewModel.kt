package com.balladie.soundtherapy.view.ui.main

import android.location.Location
import com.balladie.soundtherapy.network.model.SoundInfo
import com.balladie.soundtherapy.network.service.SoundLinkService
import com.balladie.soundtherapy.preference.AppPreference
import com.balladie.soundtherapy.view.ui.base.BaseViewModel
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val preference: AppPreference
) : BaseViewModel() {

    val adrenalineSoundLinks = ArrayList<SoundInfo>()
    val healingSoundLinks = ArrayList<SoundInfo>()
    val deepsleepSoundLinks = ArrayList<SoundInfo>()
    val focusSoundLinks = ArrayList<SoundInfo>()
    val recoverySoundLinks = ArrayList<SoundInfo>()

    fun getLastPageIdx() = preference.lastPageIdx

    fun saveLastPageIdx(idx: Int) {
        preference.setLastPageIdx(idx)
    }

    fun getSavedPauseInfo() = preference.wasPaused

    fun savePauseInfo(paused: Boolean) {
        preference.setWasPaused(paused)
    }
}