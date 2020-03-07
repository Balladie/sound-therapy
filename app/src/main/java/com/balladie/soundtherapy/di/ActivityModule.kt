package com.balladie.soundtherapy.di

import com.balladie.soundtherapy.view.ui.main.MainActivity
import com.balladie.soundtherapy.view.ui.processed.ProcessedActivity
import com.balladie.soundtherapy.view.ui.splash.SplashActivity
import com.balladie.soundtherapy.view.ui.tutorial.TutorialActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun splash(): SplashActivity

    @ContributesAndroidInjector
    abstract fun main(): MainActivity

    @ContributesAndroidInjector
    abstract fun tutorial(): TutorialActivity

    @ContributesAndroidInjector
    abstract fun processed(): ProcessedActivity
}