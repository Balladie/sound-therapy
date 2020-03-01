package com.balladie.soundtherapy.view.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
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
import com.balladie.soundtherapy.view.setThrottledOnClickListener
import com.balladie.soundtherapy.view.setWindowFullScreen
import com.balladie.soundtherapy.view.ui.base.BaseActivity
import com.flaviofaria.kenburnsview.RandomTransitionGenerator
import com.google.android.gms.location.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<MainViewModel> { viewModelFactory }

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var mediaPlayer = MediaPlayer()

    private var currentModeIdx: Int = 0
    private var currentSoundLink: String? = null
    private var pausedMode: Int = 0

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

        playing = viewModel.getSavedPauseInfo().not()

        setupBg()
        initializeMode()

        initLocationUpdates()
        startLocationUpdates()

        setupBtnListeners()
        setPlayOrPause()
    }

    override fun onResume() {
        super.onResume()
        setWindowFullScreen(window, actionBar)
    }

    private fun initLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest().apply {
            interval = 10000
            fastestInterval = 10000
            smallestDisplacement = 170f
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
        locationCallback = object : LocationCallback() {
            @SuppressLint("BinaryOperationInTimber")
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                if (locationResult.locations.isNotEmpty()) {
                    if (BuildConfig.DEBUG) {
                        Timber.d("Sending to server: " +
                                "mode=$currentModeIdx, " +
                                "latitude=${locationResult.lastLocation.latitude}, " +
                                "longitude=${locationResult.lastLocation.longitude}")
                    }
                    checkNextSound(locationResult.lastLocation)
                }
            }
        }
    }

    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun instantRestartLocationUpdates() {
        stopLocationUpdates()
        startLocationUpdates()
    }

    private fun setupBg() {
        binding.bgMain.setTransitionGenerator(RandomTransitionGenerator(5000, AccelerateDecelerateInterpolator()))
    }

    private fun setPlayOrPause() {
        if (playing.not()) {
            binding.bgMain.alpha = 0.3f
            binding.imageIconPause.visibility = View.GONE
            binding.imageIconMusic.visibility = View.VISIBLE
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
                setAnimationListener(object: Animation.AnimationListener {
                    override fun onAnimationRepeat(p0: Animation) {}
                    override fun onAnimationEnd(p0: Animation) {
                        binding.bgMain.alpha = 0.3f
                    }
                    override fun onAnimationStart(p0: Animation) {
                        pausedMode = currentModeIdx
                        pausePlayingSound()
                        binding.bgMain.alpha = 1.0f
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
                setAnimationListener(object: Animation.AnimationListener {
                    override fun onAnimationRepeat(p0: Animation) {}
                    override fun onAnimationEnd(p0: Animation) {}
                    override fun onAnimationStart(p0: Animation) {
                        if (pausedMode == currentModeIdx) {
                            resumePlayingSound()
                        }
                        else {
                            startPlayingSound()
                        }
                        binding.bgMain.alpha = 1.0f
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
        mediaPlayer.stop()

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

        instantRestartLocationUpdates()
    }

    private fun checkNextSound(location: Location) {
        viewModel.getSoundLink(currentModeIdx, location)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it[0].mode != currentModeIdx) return@subscribe
                if (it[0].link != currentSoundLink) {
                    currentSoundLink = it[0].link
                    stopPlayingSound()
                    setMediaPlayer(it[0].link)
                    if (playing) startPlayingSound()
                }
            }, {
                Timber.e(it)
            }).also { compositeDisposable.add(it) }
    }

    private fun setMediaPlayer(link: String?) {
        val uri: Uri = Uri.parse(BuildConfig.BASE_URL + link) ?: return

        try {
            mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(this@MainActivity, uri)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaPlayer.setAudioAttributes(AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build())
            } else {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            }
            mediaPlayer.isLooping = true
            mediaPlayer.prepare()
        } catch (exception: Exception) {
            Timber.e(exception)
        }
    }

    private fun startPlayingSound() {
        //mediaPlayer.setOnPreparedListener { mediaPlayer.start() }
        mediaPlayer.start()
    }

    private fun pausePlayingSound() {
        mediaPlayer.pause()
    }

    private fun resumePlayingSound() {
        mediaPlayer.seekTo(mediaPlayer.currentPosition)
        mediaPlayer.start()
    }

    private fun stopPlayingSound() {
        mediaPlayer.stop()
    }

    override fun onDestroy() {
        mediaPlayer.stop()
        viewModel.saveLastPageIdx(currentModeIdx)
        viewModel.savePauseInfo(playing.not())
        super.onDestroy()
    }
}