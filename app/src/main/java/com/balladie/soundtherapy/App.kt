package com.balladie.soundtherapy

import com.balladie.soundtherapy.di.DaggerAppComponent
import com.balladie.soundtherapy.di.NetworkModule
import com.balladie.soundtherapy.di.PreferenceModule
import com.facebook.stetho.Stetho
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber

class App : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
        DaggerAppComponent.builder()
            .application(this)
            .preferenceModule(PreferenceModule())
            .networkModule(NetworkModule(BuildConfig.BASE_URL))
            .build()

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Stetho.initializeWithDefaults(this)
        }
    }
}