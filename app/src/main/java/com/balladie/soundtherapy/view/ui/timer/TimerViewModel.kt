package com.balladie.soundtherapy.view.ui.timer

import com.balladie.soundtherapy.preference.AppPreference
import com.balladie.soundtherapy.view.ui.base.BaseViewModel
import javax.inject.Inject

class TimerViewModel @Inject constructor(
    private val preference: AppPreference
) : BaseViewModel() {}