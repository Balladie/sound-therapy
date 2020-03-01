package com.balladie.soundtherapy.view.ui.base

import android.content.Context
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.CompositeDisposable

abstract class BaseActivity : DaggerAppCompatActivity() {

    protected val compositeDisposable = CompositeDisposable()
    val context: Context get() = this

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}
