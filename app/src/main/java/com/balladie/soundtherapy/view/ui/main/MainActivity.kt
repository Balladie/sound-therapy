package com.balladie.soundtherapy.view.ui.main

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.balladie.soundtherapy.BuildConfig
import com.balladie.soundtherapy.R
import com.balladie.soundtherapy.databinding.ActivityMainBinding
import com.balladie.soundtherapy.network.model.SoundInfo
import com.balladie.soundtherapy.view.setThrottledOnClickListener
import com.balladie.soundtherapy.view.setWindowFullScreen
import com.balladie.soundtherapy.view.ui.base.BaseActivity
import com.flaviofaria.kenburnsview.RandomTransitionGenerator
import timber.log.Timber
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<MainViewModel> { viewModelFactory }

    private lateinit var binding: ActivityMainBinding
    private val mediaPlayers = arrayListOf(MediaPlayer(), MediaPlayer(), MediaPlayer(), MediaPlayer(), MediaPlayer())

    private var currentModeIdx: Int = 0
    private var pausedMode: Int = 0
    private val currentSoundIdx = arrayListOf(0, 0, 0, 0, 0)

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

        setupBg()
        initializeMode()
        getLinksFromExtra()
        initAllMediaPlayers()
        setupBtnListeners()
        setPlayOrPause()
    }

    override fun onResume() {
        super.onResume()
        setWindowFullScreen(window, actionBar)
    }

    private fun getLinksFromExtra() {
        if (intent.hasExtra("numAdrenaline")) {
            for (i in 0 until intent.getIntExtra("numAdrenaline", 0)) {
                if (intent.hasExtra("adrenaline${i}")) {
                    viewModel.adrenalineSoundLinks += SoundInfo(intent.getStringExtra("adrenaline${i}")!!, 0)
                }
            }
        }
        if (intent.hasExtra("numHealing")) {
            for (i in 0 until intent.getIntExtra("numHealing", 0)) {
                if (intent.hasExtra("healing${i}")) {
                    viewModel.healingSoundLinks += SoundInfo(intent.getStringExtra("healing${i}")!!, 1)
                }
            }
        }
        if (intent.hasExtra("numDeepsleep")) {
            for (i in 0 until intent.getIntExtra("numDeepsleep", 0)) {
                if (intent.hasExtra("deepsleep${i}")) {
                    viewModel.deepsleepSoundLinks += SoundInfo(intent.getStringExtra("deepsleep${i}")!!, 2)
                }
            }
        }
        if (intent.hasExtra("numFocus")) {
            for (i in 0 until intent.getIntExtra("numFocus", 0)) {
                if (intent.hasExtra("focus${i}")) {
                    viewModel.focusSoundLinks += SoundInfo(intent.getStringExtra("focus${i}")!!, 3)
                }
            }
        }
        if (intent.hasExtra("numRecovery")) {
            for (i in 0 until intent.getIntExtra("numRecovery", 0)) {
                if (intent.hasExtra("recovery${i}")) {
                    viewModel.recoverySoundLinks += SoundInfo(intent.getStringExtra("recovery${i}")!!, 4)
                }
            }
        }
    }

    private fun setupBg() {
        binding.bgMain.setTransitionGenerator(RandomTransitionGenerator(5000, AccelerateDecelerateInterpolator()))
    }

    private fun setPlayOrPause() {
        playing = viewModel.getSavedPauseInfo().not()

        if (playing.not()) {
            binding.bgMain.alpha = 0.3f
            binding.imageIconPause.visibility = View.GONE
            binding.imageIconMusic.visibility = View.VISIBLE
        } else {
            startPlayingSound(currentModeIdx)
        }
    }

    private fun initializeMode() {
        currentModeIdx = viewModel.getLastPageIdx()
        when (currentModeIdx) {
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
                fillAfter = true
                setAnimationListener(object: Animation.AnimationListener {
                    override fun onAnimationRepeat(p0: Animation) {}
                    override fun onAnimationEnd(p0: Animation) {
                    }
                    override fun onAnimationStart(p0: Animation) {
                        pausedMode = currentModeIdx
                        pausePlayingSound(currentModeIdx)
                    }
                })
            })
            binding.imageIconPause.visibility = View.GONE
            binding.imageIconMusic.visibility = View.VISIBLE
            playing = playing.not()
        }

        binding.imageIconMusic.setThrottledOnClickListener {
            binding.bgMain.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_bg).apply {
                duration = 500
                fillAfter = true
                setAnimationListener(object: Animation.AnimationListener {
                    override fun onAnimationRepeat(p0: Animation) {}
                    override fun onAnimationEnd(p0: Animation) {}
                    override fun onAnimationStart(p0: Animation) {
                        if (pausedMode == currentModeIdx) {
                            resumePlayingSound(currentModeIdx)
                        }
                        else {
                            startPlayingSound(currentModeIdx)
                        }
                    }
                })
            })
            binding.imageIconPause.visibility = View.VISIBLE
            binding.imageIconMusic.visibility = View.GONE
            playing = playing.not()
        }
    }

    private fun setCurrentMode(idx: Int) {
        val pastModeIdx = currentModeIdx
        currentModeIdx = idx
        stopPlayingSound(pastModeIdx)

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

        binding.textModeMain.text = arrTitle[currentModeIdx]
        binding.textModeMain.setTextColor(arrTitleColor[currentModeIdx])
        binding.bgMain.setImageResource(arrResource[currentModeIdx])

        arrSelected[pastModeIdx].visibility = View.INVISIBLE
        arrSelected[currentModeIdx].visibility = View.VISIBLE
        arrUnselected[pastModeIdx].visibility = View.VISIBLE
        arrUnselected[currentModeIdx].visibility = View.INVISIBLE

        if (playing) {
            startPlayingSound(currentModeIdx)
        }
    }

    private fun initAllMediaPlayers() {
        for (i in 0..4) {
            getReadyMediaPlayer(i)
        }
    }

    private fun getReadyMediaPlayer(modeIdx: Int) {
        val arrSoundLinks = arrayOf(
            viewModel.adrenalineSoundLinks,
            viewModel.healingSoundLinks,
            viewModel.deepsleepSoundLinks,
            viewModel.focusSoundLinks,
            viewModel.recoverySoundLinks
        )

        val uri = Uri.parse(BuildConfig.BASE_URL + arrSoundLinks[modeIdx][currentSoundIdx[modeIdx]].link)

        try {
            mediaPlayers[modeIdx] = MediaPlayer()
            mediaPlayers[modeIdx].setDataSource(this@MainActivity, uri)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaPlayers[modeIdx].setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
            } else {
                mediaPlayers[modeIdx].setAudioStreamType(AudioManager.STREAM_MUSIC)
            }
            mediaPlayers[modeIdx].setOnCompletionListener {
                currentSoundIdx[modeIdx] = if (currentSoundIdx[modeIdx] == arrSoundLinks[modeIdx].size - 1) {
                    0
                } else {
                    currentSoundIdx[modeIdx] + 1
                }
                it.release()
                getReadyMediaPlayer(modeIdx)
            }
        } catch (exception: Exception) {
            Timber.e(exception)
        }
    }

    private fun startPlayingSound(modeIdx: Int) {
        mediaPlayers[modeIdx].prepareAsync()
        mediaPlayers[modeIdx].setOnPreparedListener {
            it.start()
        }
    }

    private fun pausePlayingSound(modeIdx: Int) {
        mediaPlayers[modeIdx].pause()
    }

    private fun resumePlayingSound(modeIdx: Int) {
        mediaPlayers[modeIdx].seekTo(mediaPlayers[modeIdx].currentPosition)
        mediaPlayers[modeIdx].start()
    }

    private fun stopPlayingSound(modeIdx: Int) {
        mediaPlayers[modeIdx].stop()
    }

    override fun onDestroy() {
        for (mediaPlayer in mediaPlayers) {
            mediaPlayer.release()
        }
        viewModel.saveLastPageIdx(currentModeIdx)
        viewModel.savePauseInfo(playing.not())
        super.onDestroy()
    }
}