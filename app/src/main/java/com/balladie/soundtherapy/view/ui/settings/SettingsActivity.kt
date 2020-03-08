package com.balladie.soundtherapy.view.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.balladie.soundtherapy.R
import com.balladie.soundtherapy.databinding.ActivitySettingsBinding
import com.balladie.soundtherapy.view.setThrottledOnClickListener
import com.balladie.soundtherapy.view.setWindowFullScreen
import com.balladie.soundtherapy.view.ui.base.BaseActivity
import javax.inject.Inject

class SettingsActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<SettingsViewModel> { viewModelFactory }

    private lateinit var binding: ActivitySettingsBinding

    companion object {
        fun intent(context: Context): Intent =
            Intent(context, SettingsActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)

        setupBtns()
    }

    override fun onResume() {
        super.onResume()
        setWindowFullScreen(window, actionBar)
    }

    private fun setupBtns() {
        binding.imageExitSettings.setThrottledOnClickListener {
            finish()
        }
    }
}