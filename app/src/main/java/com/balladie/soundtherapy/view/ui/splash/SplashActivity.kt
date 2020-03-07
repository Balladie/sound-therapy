package com.balladie.soundtherapy.view.ui.splash

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.balladie.soundtherapy.R
import com.balladie.soundtherapy.databinding.ActivityWelcomeBinding
import com.balladie.soundtherapy.view.setThrottledOnClickListener
import com.balladie.soundtherapy.view.setWindowFullScreen
import com.balladie.soundtherapy.view.ui.base.BaseActivity
import com.balladie.soundtherapy.view.ui.processed.ProcessedActivity
import com.balladie.soundtherapy.view.ui.tutorial.TutorialActivity
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.schedule

class SplashActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<SplashViewModel> { viewModelFactory }

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome)

        setWelcomeInvisible()
        setAnimation()
    }

    override fun onResume() {
        super.onResume()
        setWindowFullScreen(window, actionBar)
    }

    private fun setAnimation() {
        binding.layoutSplash.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in).apply {
            duration = 2000
            setAnimationListener(object: Animation.AnimationListener {
                override fun onAnimationRepeat(p0: Animation) {}
                override fun onAnimationEnd(p0: Animation) {
                    Timer().schedule(1500) {
                        moveToNext()
                    }
                }
                override fun onAnimationStart(p0: Animation) {}
            })
        })
    }

    private fun moveToNext() {
        if (viewModel.isFirstLogin()) {
            Thread(Runnable {
                this@SplashActivity.runOnUiThread(Runnable {
                    setWelcomeAnimation()
                    setWelcomeVisible()
                    setBtnOnClickListeners()
                })
            }).start()
        } else {
            startActivity(ProcessedActivity.intent(context))
            finish()
        }
    }

    private fun setWelcomeInvisible() {
        binding.imageBtnContinueBgWelcome.visibility = View.GONE
        binding.imageBtnContinueTextWelcome.visibility = View.GONE
        binding.imageTextWelcomeTo.visibility = View.GONE
    }

    private fun setWelcomeVisible() {
        binding.imageBtnContinueBgWelcome.visibility = View.VISIBLE
        binding.imageBtnContinueTextWelcome.visibility = View.VISIBLE
        binding.imageTextWelcomeTo.visibility = View.VISIBLE
    }

    private fun setWelcomeAnimation() {
        binding.imageBtnContinueBgWelcome.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
        binding.imageBtnContinueTextWelcome.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
        binding.imageTextWelcomeTo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
    }

    private fun setBtnOnClickListeners() {
        binding.imageBtnContinueBgWelcome.setThrottledOnClickListener {
            startActivity(TutorialActivity.intent(context))
            finish()
        }
        binding.imageBtnContinueTextWelcome.setThrottledOnClickListener {
            startActivity(TutorialActivity.intent(context))
            finish()
        }
    }
}
