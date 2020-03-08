package com.balladie.soundtherapy.view.ui.timer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.balladie.soundtherapy.R
import com.balladie.soundtherapy.databinding.ActivityTimerBinding
import com.balladie.soundtherapy.view.setThrottledOnClickListener
import com.balladie.soundtherapy.view.setWindowFullScreen
import com.balladie.soundtherapy.view.ui.base.BaseActivity
import dagger.android.support.DaggerAppCompatActivity
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

        setupBtns()
    }

    override fun onResume() {
        super.onResume()
        setWindowFullScreen(window, actionBar)
    }

    private fun setupBtns() {
        binding.imageBtnCancelTimer.setThrottledOnClickListener {
            finish()
        }
        binding.imageBtnSetTimer.setThrottledOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                passTimeToPreviousActivity(
                    binding.timepickerTimer.hour,
                    binding.timepickerTimer.minute,
                    binding.switchAlarmSound.isChecked
                )
            } else {
                passTimeToPreviousActivity(
                    binding.timepickerTimer.currentHour,
                    binding.timepickerTimer.currentMinute,
                    binding.switchAlarmSound.isChecked
                )
            }
            finish()
        }
    }

    private fun passTimeToPreviousActivity(hour: Int, minute: Int, enableSound: Boolean) {
        val resultIntent = Intent()
        resultIntent.putExtra("hour", hour)
        resultIntent.putExtra("minute", minute)
        resultIntent.putExtra("enableSound", enableSound)
        setResult(Activity.RESULT_OK, resultIntent)
    }
}