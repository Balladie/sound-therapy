package com.balladie.soundtherapy.view.ui.processed

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.balladie.soundtherapy.BuildConfig
import com.balladie.soundtherapy.R
import com.balladie.soundtherapy.databinding.ActivityProcessedBinding
import com.balladie.soundtherapy.view.setWindowFullScreen
import com.balladie.soundtherapy.view.ui.base.BaseActivity
import com.balladie.soundtherapy.view.ui.main.MainActivity
import com.google.android.gms.location.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class ProcessedActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<ProcessedViewModel> { viewModelFactory }

    private lateinit var binding: ActivityProcessedBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    companion object {
        fun intent(context: Context): Intent =
            Intent(context, ProcessedActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_processed)

        initLocationUpdates()
        setAnimation()
    }

    override fun onResume() {
        super.onResume()
        setWindowFullScreen(window, actionBar)
    }

    private fun moveToNext() {
        val intent = MainActivity.intent(this)
        intent.putExtra("numAdrenaline", viewModel.adrenalineSoundLinks.size)
        for (i in viewModel.adrenalineSoundLinks.indices) {
            intent.putExtra("adrenaline${i}", viewModel.adrenalineSoundLinks[i].link)
        }
        intent.putExtra("numHealing", viewModel.healingSoundLinks.size)
        for (i in viewModel.healingSoundLinks.indices) {
            intent.putExtra("healing${i}", viewModel.healingSoundLinks[i].link)
        }
        intent.putExtra("numDeepsleep", viewModel.deepsleepSoundLinks.size)
        for (i in viewModel.deepsleepSoundLinks.indices) {
            intent.putExtra("deepsleep${i}", viewModel.deepsleepSoundLinks[i].link)
        }
        intent.putExtra("numFocus", viewModel.focusSoundLinks.size)
        for (i in viewModel.focusSoundLinks.indices) {
            intent.putExtra("focus${i}", viewModel.focusSoundLinks[i].link)
        }
        intent.putExtra("numRecovery", viewModel.recoverySoundLinks.size)
        for (i in viewModel.recoverySoundLinks.indices) {
            intent.putExtra("recovery${i}", viewModel.recoverySoundLinks[i].link)
        }
        startActivity(intent)
        finish()
    }

    private fun setAnimation() {
        binding.imageSpinnerDots.startAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in).apply {
            interpolator = OvershootInterpolator()
            fillAfter = true
            setAnimationListener(object: Animation.AnimationListener {
                override fun onAnimationRepeat(p0: Animation) {}
                override fun onAnimationEnd(p0: Animation) {
                    binding.imageSpinnerRound.startAnimation(AnimationUtils.loadAnimation(this@ProcessedActivity, R.anim.zoom_in).apply {
                        interpolator = OvershootInterpolator()
                        fillAfter = true
                        setAnimationListener(object: Animation.AnimationListener {
                            override fun onAnimationRepeat(p0: Animation) {}
                            override fun onAnimationEnd(p0: Animation) {
                                binding.textFindingBest.startAnimation(AnimationUtils.loadAnimation(this@ProcessedActivity, R.anim.fade_in).apply {
                                    fillAfter = true
                                    setAnimationListener(object: Animation.AnimationListener {
                                        override fun onAnimationRepeat(p0: Animation) {}
                                        override fun onAnimationEnd(p0: Animation) {
                                            binding.textSoundsForYou.startAnimation(AnimationUtils.loadAnimation(this@ProcessedActivity, R.anim.fade_in).apply {
                                                fillAfter = true
                                                setAnimationListener(object: Animation.AnimationListener {
                                                    override fun onAnimationRepeat(p0: Animation) {}
                                                    override fun onAnimationEnd(p0: Animation) {
                                                        rotateSpinners()
                                                        startSoundProcess()
                                                    }
                                                    override fun onAnimationStart(p0: Animation) {}
                                                })
                                            })
                                        }
                                        override fun onAnimationStart(p0: Animation) {}
                                    })
                                })
                            }
                            override fun onAnimationStart(p0: Animation) {}
                        })
                    })
                }
                override fun onAnimationStart(p0: Animation) {}
            })
        })
    }

    private fun rotateSpinners() {
        binding.imageSpinnerRound.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_once).apply {
            interpolator = AccelerateDecelerateInterpolator()
            repeatCount = Animation.INFINITE
            setAnimationListener(object: Animation.AnimationListener {
                override fun onAnimationRepeat(p0: Animation) {}
                override fun onAnimationEnd(p0: Animation) {}
                override fun onAnimationStart(p0: Animation) {}
            })
        })
        binding.imageSpinnerDots.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_once_counterclock).apply {
            repeatCount = Animation.INFINITE
            setAnimationListener(object: Animation.AnimationListener {
                override fun onAnimationRepeat(p0: Animation) {}
                override fun onAnimationEnd(p0: Animation) {}
                override fun onAnimationStart(p0: Animation) {}
            })
        })
    }

    private fun startSoundProcess() {
        startLocationUpdates()
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
                        Timber.d("Got current location: " +
                                "latitude=${locationResult.lastLocation.latitude}, " +
                                "longitude=${locationResult.lastLocation.longitude}")
                    }
                    getSoundLinks(locationResult.lastLocation)
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

    private fun getSoundLinks(location: Location) {
        viewModel.getSoundLink(0, location)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewModel.adrenalineSoundLinks = ArrayList(it)
                Timber.d("${viewModel.adrenalineSoundLinks.size}")
                viewModel.dones[0] = true
                if (viewModel.dones.all { done -> done }) {
                    moveToNext()
                }
            }, {
                Timber.e(it)
            }).also { compositeDisposable.add(it) }
        viewModel.getSoundLink(1, location)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewModel.healingSoundLinks = ArrayList(it)
                Timber.d("${viewModel.healingSoundLinks.size}")
                viewModel.dones[1] = true
                if (viewModel.dones.all { done -> done }) {
                    moveToNext()
                }
            }, {
                Timber.e(it)
            }).also { compositeDisposable.add(it) }
        viewModel.getSoundLink(2, location)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewModel.deepsleepSoundLinks = ArrayList(it)
                Timber.d("${viewModel.deepsleepSoundLinks.size}")
                viewModel.dones[2] = true
                if (viewModel.dones.all { done -> done }) {
                    moveToNext()
                }
            }, {
                Timber.e(it)
            }).also { compositeDisposable.add(it) }
        viewModel.getSoundLink(3, location)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewModel.focusSoundLinks = ArrayList(it)
                Timber.d("${viewModel.focusSoundLinks.size}")
                viewModel.dones[3] = true
                if (viewModel.dones.all { done -> done }) {
                    moveToNext()
                }
            }, {
                Timber.e(it)
            }).also { compositeDisposable.add(it) }
        viewModel.getSoundLink(4, location)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewModel.recoverySoundLinks = ArrayList(it)
                Timber.d("${viewModel.recoverySoundLinks.size}")
                viewModel.dones[4] = true
                if (viewModel.dones.all { done -> done }) {
                    moveToNext()
                }
            }, {
                Timber.e(it)
            }).also { compositeDisposable.add(it) }
    }
}