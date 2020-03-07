package com.balladie.soundtherapy.view.ui.timer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.balladie.soundtherapy.R
import com.balladie.soundtherapy.databinding.ActivityTimerBinding
import com.balladie.soundtherapy.view.ui.base.BaseActivity
import javax.inject.Inject

class TimerActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<TimerViewModel> { viewModelFactory }

    private lateinit var binding: ActivityTimerBinding

    companion object {
        fun intent(context: Context): Intent =
            Intent(context, TimerActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_timer)
    }
}