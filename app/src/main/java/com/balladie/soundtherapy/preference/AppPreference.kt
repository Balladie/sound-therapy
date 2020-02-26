package com.balladie.soundtherapy.preference

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreference @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    val isOldUser: Boolean get() = _isOldUser ?: false
    private var _isOldUser by prefBoolean(sharedPreferences, PrefKey.FISRTLOGIN)

    val lastPageIdx: Int get() = _lastPageIdx ?: 0
    private var _lastPageIdx by prefInt(sharedPreferences, PrefKey.LASTPAGEIDX)

    val wasPaused: Boolean get() = _wasPaused ?: false
    private var _wasPaused by prefBoolean(sharedPreferences, PrefKey.WASPLAYING)

    fun setLoginOn() {
        _isOldUser = true
    }

    fun setLastPageIdx(idx: Int) {
        _lastPageIdx = idx
    }

    fun setWasPaused(paused: Boolean) {
        _wasPaused = paused
    }

    fun clear() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }
}