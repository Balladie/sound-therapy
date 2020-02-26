package com.balladie.soundtherapy.di

import com.balladie.soundtherapy.view.ui.tutorial.adrenaline.AdrenalineFragment
import com.balladie.soundtherapy.view.ui.tutorial.deepsleep.DeepsleepFragment
import com.balladie.soundtherapy.view.ui.tutorial.focus.FocusFragment
import com.balladie.soundtherapy.view.ui.tutorial.healing.HealingFragment
import com.balladie.soundtherapy.view.ui.tutorial.recovery.RecoveryFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class TutorialActivityModule {

    @ContributesAndroidInjector
    abstract fun adrenaline(): AdrenalineFragment

    @ContributesAndroidInjector
    abstract fun healing(): HealingFragment

    @ContributesAndroidInjector
    abstract fun deepsleep(): DeepsleepFragment

    @ContributesAndroidInjector
    abstract fun focus(): FocusFragment

    @ContributesAndroidInjector
    abstract fun recovery(): RecoveryFragment
}