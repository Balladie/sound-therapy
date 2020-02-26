package com.balladie.soundtherapy.view.ui.main

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.balladie.soundtherapy.R
import com.balladie.soundtherapy.databinding.ActivityMainBinding
import com.balladie.soundtherapy.view.setThrottledOnClickListener
import com.balladie.soundtherapy.view.setWindowFullScreen
import com.balladie.soundtherapy.view.ui.base.BaseActivity
import com.flaviofaria.kenburnsview.RandomTransitionGenerator
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<MainViewModel> { viewModelFactory }

    private lateinit var binding: ActivityMainBinding
    private var currentModeIdx: Int = 0

    companion object {
        fun intent(context: Context): Intent =
            Intent(context, MainActivity::class.java)

        private var playing: Boolean = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        playing = viewModel.getSavedPauseInfo()

        setupBg()
        initializeMode()
        setupBtnListeners()
    }

    override fun onResume() {
        super.onResume()
        setWindowFullScreen(window, actionBar)
    }

    private fun setupBg() {
        if (playing.not()) {
            binding.bgMain.alpha = 0.0f
        }
        binding.bgMain.setTransitionGenerator(RandomTransitionGenerator(5000, AccelerateDecelerateInterpolator()))
    }

    private fun initializeMode() {
        val idx = viewModel.getLastPageIdx()
        when (idx) {
            0 -> {
                binding.textModeMain.text = getString(R.string.text_title_adrenaline)
                binding.textModeMain.setTextColor(Color.parseColor("#ffffff"))
                binding.bgMain.setImageResource(R.drawable.bg_adrenaline)
                binding.imageIconAdrenalineUnselected.visibility = View.INVISIBLE
                binding.imageIconHealingSelected.visibility = View.INVISIBLE
                binding.imageIconDeepsleepSelected.visibility = View.INVISIBLE
                binding.imageIconFocusSelected.visibility = View.INVISIBLE
                binding.imageIconRecoverySelected.visibility = View.INVISIBLE
            }
            1 -> {
                binding.textModeMain.text = getString(R.string.text_title_healing)
                binding.textModeMain.setTextColor(Color.parseColor("#6ed0b8"))
                binding.bgMain.setImageResource(R.drawable.bg_healing)
                binding.imageIconAdrenalineSelected.visibility = View.INVISIBLE
                binding.imageIconHealingUnselected.visibility = View.INVISIBLE
                binding.imageIconDeepsleepSelected.visibility = View.INVISIBLE
                binding.imageIconFocusSelected.visibility = View.INVISIBLE
                binding.imageIconRecoverySelected.visibility = View.INVISIBLE
            }
            2-> {
                binding.textModeMain.text = getString(R.string.text_title_deepsleep)
                binding.textModeMain.setTextColor(Color.parseColor("#96a3ff"))
                binding.bgMain.setImageResource(R.drawable.bg_deepsleep)
                binding.imageIconAdrenalineSelected.visibility = View.INVISIBLE
                binding.imageIconHealingSelected.visibility = View.INVISIBLE
                binding.imageIconDeepsleepUnselected.visibility = View.INVISIBLE
                binding.imageIconFocusSelected.visibility = View.INVISIBLE
                binding.imageIconRecoverySelected.visibility = View.INVISIBLE
            }
            3 -> {
                binding.textModeMain.text = getString(R.string.text_title_focus)
                binding.textModeMain.setTextColor(Color.parseColor("#ffffff"))
                binding.bgMain.setImageResource(R.drawable.bg_focus)
                binding.imageIconAdrenalineSelected.visibility = View.INVISIBLE
                binding.imageIconHealingSelected.visibility = View.INVISIBLE
                binding.imageIconDeepsleepSelected.visibility = View.INVISIBLE
                binding.imageIconFocusUnselected.visibility = View.INVISIBLE
                binding.imageIconRecoverySelected.visibility = View.INVISIBLE
            }
            4 -> {
                binding.textModeMain.text = getString(R.string.text_title_recovery)
                binding.textModeMain.setTextColor(Color.parseColor("#ffffff"))
                binding.bgMain.setImageResource(R.drawable.bg_recovery)
                binding.imageIconAdrenalineSelected.visibility = View.INVISIBLE
                binding.imageIconHealingSelected.visibility = View.INVISIBLE
                binding.imageIconDeepsleepSelected.visibility = View.INVISIBLE
                binding.imageIconFocusSelected.visibility = View.INVISIBLE
                binding.imageIconRecoveryUnselected.visibility = View.INVISIBLE
            }
        }
        currentModeIdx = idx
    }

    private fun setupBtnListeners() {
        binding.imageIconAdrenalineUnselected.setThrottledOnClickListener {
            setCurrentMode(0)
        }
        binding.imageIconHealingUnselected.setThrottledOnClickListener {
            setCurrentMode(1)
        }
        binding.imageIconDeepsleepUnselected.setThrottledOnClickListener {
            setCurrentMode(2)
        }
        binding.imageIconFocusUnselected.setThrottledOnClickListener {
            setCurrentMode(3)
        }
        binding.imageIconRecoveryUnselected.setThrottledOnClickListener {
            setCurrentMode(4)
        }

        binding.imageIconPause.setThrottledOnClickListener {
            binding.bgMain.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out_bg).apply {
                duration = 500
                setAnimationListener(object: Animation.AnimationListener {
                    override fun onAnimationRepeat(p0: Animation) {}
                    override fun onAnimationEnd(p0: Animation) {
                        binding.bgMain.alpha = 0.3f
                    }
                    override fun onAnimationStart(p0: Animation) {
                        binding.bgMain.alpha = 1.0f
                    }
                })
            })
            binding.imageIconPause.visibility = View.GONE
            binding.imageIconMusic.visibility = View.VISIBLE
        }

        binding.imageIconMusic.setThrottledOnClickListener {
            binding.bgMain.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_bg).apply {
                duration = 500
                setAnimationListener(object: Animation.AnimationListener {
                    override fun onAnimationRepeat(p0: Animation) {}
                    override fun onAnimationEnd(p0: Animation) {}
                    override fun onAnimationStart(p0: Animation) {
                        binding.bgMain.alpha = 1.0f
                    }
                })
            })
            binding.imageIconPause.visibility = View.VISIBLE
            binding.imageIconMusic.visibility = View.GONE
        }
    }

    private fun setCurrentMode(idx: Int) {
        val arrTitle = arrayOf(
            getString(R.string.text_title_adrenaline),
            getString(R.string.text_title_healing),
            getString(R.string.text_title_deepsleep),
            getString(R.string.text_title_focus),
            getString(R.string.text_title_recovery)
        )
        val arrTitleColor = arrayOf(
            Color.parseColor("#ffffff"),
            Color.parseColor("#6ed0b8"),
            Color.parseColor("#96a3ff"),
            Color.parseColor("#ffffff"),
            Color.parseColor("#ffffff")
        )
        val arrResource = arrayOf(
            R.drawable.bg_adrenaline,
            R.drawable.bg_healing,
            R.drawable.bg_deepsleep,
            R.drawable.bg_focus,
            R.drawable.bg_recovery
        )
        val arrSelected = arrayOf(
            binding.imageIconAdrenalineSelected,
            binding.imageIconHealingSelected,
            binding.imageIconDeepsleepSelected,
            binding.imageIconFocusSelected,
            binding.imageIconRecoverySelected
        )
        val arrUnselected = arrayOf(
            binding.imageIconAdrenalineUnselected,
            binding.imageIconHealingUnselected,
            binding.imageIconDeepsleepUnselected,
            binding.imageIconFocusUnselected,
            binding.imageIconRecoveryUnselected
        )

        binding.textModeMain.text = arrTitle[idx]
        binding.textModeMain.setTextColor(arrTitleColor[idx])
        binding.bgMain.setImageResource(arrResource[idx])

        arrSelected[currentModeIdx].visibility = View.INVISIBLE
        arrSelected[idx].visibility = View.VISIBLE
        arrUnselected[currentModeIdx].visibility = View.VISIBLE
        arrUnselected[idx].visibility = View.INVISIBLE

        currentModeIdx = idx
    }

    override fun onDestroy() {
        viewModel.saveLastPageIdx(currentModeIdx)
        viewModel.savePauseInfo(playing.not())
        super.onDestroy()
    }
}