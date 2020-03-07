package com.balladie.soundtherapy.view.ui.processed

import android.location.Location
import com.balladie.soundtherapy.network.model.SoundInfo
import com.balladie.soundtherapy.network.service.SoundLinkService
import com.balladie.soundtherapy.view.ui.base.BaseViewModel
import io.reactivex.Observable
import javax.inject.Inject
import kotlin.properties.Delegates

class ProcessedViewModel @Inject constructor(
    private val soundLinkService: SoundLinkService
) : BaseViewModel() {

    var dones = arrayListOf(false, false, false, false, false)

    var adrenalineSoundLinks = ArrayList<SoundInfo>()
    var healingSoundLinks = ArrayList<SoundInfo>()
    var deepsleepSoundLinks = ArrayList<SoundInfo>()
    var focusSoundLinks = ArrayList<SoundInfo>()
    var recoverySoundLinks = ArrayList<SoundInfo>()

    fun getSoundLink(mode: Int, location: Location) =
        soundLinkService.getSoundLink(mode, location.latitude, location.longitude)
}