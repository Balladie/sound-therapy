package com.balladie.soundtherapy.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.balladie.soundtherapy.view.ui.splash.SplashViewModel
import com.balladie.soundtherapy.view.ui.tutorial.TutorialViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun splash(splashViewModel: SplashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TutorialViewModel::class)
    abstract fun tutorial(tutorialViewModel: TutorialViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}