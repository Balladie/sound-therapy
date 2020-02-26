package com.balladie.soundtherapy.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PreferenceModule(private val name: String? = null) {

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences = name?.let {
        context.getSharedPreferences(name,Context.MODE_PRIVATE)
    } ?: PreferenceManager.getDefaultSharedPreferences(context)
}