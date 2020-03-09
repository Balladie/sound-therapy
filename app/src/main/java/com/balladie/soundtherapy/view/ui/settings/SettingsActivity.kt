package com.balladie.soundtherapy.view.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
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

        // Account
        binding.textCircleAccount.setThrottledOnClickListener {
            accountSettings()
        }
        binding.textNameAccount.setThrottledOnClickListener {
            accountSettings()
        }
        binding.textSubscribedAccount.setThrottledOnClickListener {
            accountSettings()
        }

        // Logout
        binding.imageBtnLogoutBg.setThrottledOnClickListener {
            logoutAccount()
        }
        binding.imageBtnLogoutImage.setThrottledOnClickListener {
            logoutAccount()
        }

        // Health Access
        binding.imageBtnHealthAllowedBgSettings.setThrottledOnClickListener {
            manageHealthAccess()
        }
        binding.imageBtnHealthAllowedText.setThrottledOnClickListener {
            manageHealthAccess()
        }

        // Remove data
        binding.imageBtnRemoveDataBg.setThrottledOnClickListener {
            removePersonalData()
        }
    }

    // Will be updated: Account
    private fun accountSettings() {
        Toast.makeText(this, "Will be updated soon, Stay tuned!", Toast.LENGTH_SHORT).show()
    }
    private fun logoutAccount() {
        Toast.makeText(this, "Will be updated soon, Stay tuned!", Toast.LENGTH_SHORT).show()
    }

    // Will be updated: Health Access
    private fun manageHealthAccess() {
        Toast.makeText(this, "Yeah you're right, it's not allowed actually. Will be updated soon, Stay tuned!", Toast.LENGTH_LONG).show()
    }

    private fun removePersonalData() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}