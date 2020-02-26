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

    fun setLoginOn() {
        _isOldUser = true
    }

    fun clear() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }
}