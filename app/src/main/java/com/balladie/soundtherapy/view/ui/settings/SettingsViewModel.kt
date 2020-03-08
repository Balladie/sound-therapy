package com.balladie.soundtherapy.view.ui.settings

import com.balladie.soundtherapy.preference.AppPreference
import com.balladie.soundtherapy.view.ui.base.BaseViewModel
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val preference: AppPreference
) : BaseViewModel() {}