package com.balladie.soundtherapy.view.ui.main

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
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
import com.balladie.soundtherapy.view.ui.settings.SettingsActivity
import com.balladie.soundtherapy.view.ui.timer.TimerActivity
import com.flaviofaria.kenburnsview.RandomTransitionGenerator
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<MainViewModel> { viewModelFactory }

    private lateinit var binding: ActivityMainBinding
    private lateinit var alarmManager: AlarmManager
    private val mediaPlayers = arrayListOf(MediaPlayer(), MediaPlayer(), MediaPlayer(), MediaPlayer(), MediaPlayer())
    private var pendingIntent: PendingIntent? = null
    private val timerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Toast.makeText(context, "Alarm! Paused playing sound.", Toast.LENGTH_SHORT).show()
            if (playing) {
                pausePlayingSound(currentModeIdx)
            }

            alarmOn = false
            binding.imageIconAlarmBg.clearAnimation()
            binding.imageIconAlarmBg.animate().cancel()
            binding.imageIconAlarmBg.alpha = 1.0f
        }
    }
    private val timerReceiverWithSound = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Toast.makeText(context, "Alarm! Paused playing sound.", Toast.LENGTH_SHORT).show()
            if (playing) {
                pausePlayingSound(currentModeIdx)
            }

            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val alarmSoundPlayer = MediaPlayer.create(context, notification)
            alarmSoundPlayer.start()

            alarmOn = false
            binding.imageIconAlarmBg.clearAnimation()
            binding.imageIconAlarmBg.animate().cancel()
            binding.imageIconAlarmBg.alpha = 1.0f
        }
    }

    private var currentModeIdx: Int = 0
    private var pausedMode: Int = 0
    private val currentSoundIdx = arrayListOf(0, 0, 0, 0, 0)

    companion object {
        fun intent(context: Context): Intent =
            Intent(context, MainActivity::class.java)

        private var playing: Boolean = true
        private var alarmOn: Boolean = false
        private const val TIMER_INTENT_VALUE = 99
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        registerReceiver(timerReceiverWithSound, IntentFilter("timerReceiverWithSound"))
        registerReceiver(timerReceiver, IntentFilter("timerReceiver"))

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
            binding.bgMain.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out_bg).apply {
                duration = 200
                fillAfter = true
                setAnimationListener(object: Animation.AnimationListener {
                    override fun onAnimationRepeat(p0: Animation) {}
                    override fun onAnimationEnd(p0: Animation) {}
                    override fun onAnimationStart(p0: Animation) {}
                })
            })
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
            pausePlayingSound(currentModeIdx)
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

        binding.imageIconAlarmBg.setThrottledOnClickListener {
            if (alarmOn) {
                alarmOn = false
                binding.imageIconAlarmBg.clearAnimation()
                binding.imageIconAlarmBg.animate().cancel()
                binding.imageIconAlarmBg.alpha = 1.0f
            } else {
                startActivityForResult(TimerActivity.intent(this), TIMER_INTENT_VALUE)
            }
        }

        binding.imageIconSettings.setThrottledOnClickListener {
            startActivity(SettingsActivity.intent(this))
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
                if (playing) {
                    startPlayingSound(modeIdx)
                }
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
        binding.bgMain.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out_bg).apply {
            duration = 500
            fillAfter = true
            setAnimationListener(object: Animation.AnimationListener {
                override fun onAnimationRepeat(p0: Animation) {}
                override fun onAnimationEnd(p0: Animation) {}
                override fun onAnimationStart(p0: Animation) {
                    pausedMode = currentModeIdx
                    mediaPlayers[modeIdx].pause()
                }
            })
        })
        binding.imageIconPause.visibility = View.GONE
        binding.imageIconMusic.visibility = View.VISIBLE
        playing = playing.not()
    }

    private fun resumePlayingSound(modeIdx: Int) {
        mediaPlayers[modeIdx].seekTo(mediaPlayers[modeIdx].currentPosition)
        mediaPlayers[modeIdx].start()
    }

    private fun stopPlayingSound(modeIdx: Int) {
        mediaPlayers[modeIdx].stop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            TIMER_INTENT_VALUE -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (data == null) {
                        Timber.e("Timer data is null...")
                        Toast.makeText(this, "Error setting timer: data received null.", Toast.LENGTH_SHORT).show()
                        return
                    } else {
                        Timber.d("${data.getIntExtra("hour", 0)}, ${data.getIntExtra("minute", 0)}, ${data.getBooleanExtra("enableSound", false)}")
                        setTimer(
                            data.getIntExtra("hour", 0),
                            data.getIntExtra("minute", 0),
                            data.getBooleanExtra("enableSound", false)
                        )
                    }
                } else {
                    Timber.d("resultCode: $resultCode")
                }
            }
        }
    }

    private fun setTimer(hour: Int, minute: Int, enableSound: Boolean) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        pendingIntent = if (enableSound) {
            PendingIntent.getBroadcast(this, 0, Intent("timerReceiverWithSound"), 0)
        } else {
            PendingIntent.getBroadcast(this, 0, Intent("timerReceiver"), 0)
        }
        alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC, calendar.timeInMillis, pendingIntent)
        alarmOn = true
        setAlarmIconAnimate()
    }

    private fun setAlarmIconAnimate() {
        val animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in).apply {
            duration = 500
            fillAfter = true
        }
        val animFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out).apply {
            duration = 500
            fillAfter = true
        }
        animFadeIn.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation) {}
            override fun onAnimationEnd(p0: Animation) {
                if (alarmOn) {
                    binding.imageIconAlarmBg.startAnimation(animFadeOut)
                }
            }
            override fun onAnimationStart(p0: Animation) {}
        })
        animFadeOut.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation) {}
            override fun onAnimationEnd(p0: Animation) {
                if (alarmOn) {
                    binding.imageIconAlarmBg.startAnimation(animFadeIn)
                }
            }
            override fun onAnimationStart(p0: Animation) {}
        })

        binding.imageIconAlarmBg.startAnimation(animFadeOut)
    }

    override fun onDestroy() {
        for (mediaPlayer in mediaPlayers) {
            mediaPlayer.release()
        }

        viewModel.saveLastPageIdx(currentModeIdx)
        viewModel.savePauseInfo(playing.not())

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
        unregisterReceiver(timerReceiverWithSound)
        unregisterReceiver(timerReceiver)

        super.onDestroy()
    }
}