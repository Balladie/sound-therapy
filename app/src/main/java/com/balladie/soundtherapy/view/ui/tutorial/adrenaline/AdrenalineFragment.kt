package com.balladie.soundtherapy.view.ui.tutorial.adrenaline

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.balladie.soundtherapy.R
import com.balladie.soundtherapy.databinding.FragmentAdrenalineBinding
import com.balladie.soundtherapy.view.setThrottledOnClickListener
import com.balladie.soundtherapy.view.ui.base.BaseFragment
import com.balladie.soundtherapy.view.ui.processed.ProcessedActivity
import com.balladie.soundtherapy.view.ui.tutorial.TutorialActivity

class AdrenalineFragment : BaseFragment() {

    companion object {
        fun create() = AdrenalineFragment()

        lateinit var binding: FragmentAdrenalineBinding

        const val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = DataBindingUtil.inflate<FragmentAdrenalineBinding>(
        inflater, R.layout.fragment_adrenaline, container, false
    ).also { binding = it }.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val context = context ?: return

        setupViews(context)
    }

    private fun setupViews(context: Context) {
        if (TutorialActivity.gotHealthAccess) {
            binding.imageBtnHealthAccessText.isClickable = false
            binding.imageBtnHealthAccessText.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out).apply {
                duration = 500
                fillAfter = true
                setAnimationListener(object: Animation.AnimationListener {
                    override fun onAnimationRepeat(p0: Animation) {}
                    override fun onAnimationEnd(p0: Animation) {}
                    override fun onAnimationStart(p0: Animation) {}
                })
            })
            binding.imageBtnHealthAccessBg.isClickable = false
            binding.imageBtnHealthAccessBg.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out).apply {
                duration = 500
                fillAfter = true
                setAnimationListener(object: Animation.AnimationListener {
                    override fun onAnimationRepeat(p0: Animation) {}
                    override fun onAnimationEnd(p0: Animation) {
                        if (TutorialActivity.gotHealthAccess && TutorialActivity.gotLocationAccess) {
                            TutorialActivity.finishedTutorial = true
                            startActivity(ProcessedActivity.intent(context))
                            activity?.finish()
                        }
                    }
                    override fun onAnimationStart(p0: Animation) {}
                })
            })
        }
        if (TutorialActivity.gotLocationAccess) {
            binding.imageBtnLocationAccess.isClickable = false
            binding.imageBtnLocationAccess.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out).apply {
                duration = 500
                fillAfter = true
                setAnimationListener(object: Animation.AnimationListener {
                    override fun onAnimationRepeat(p0: Animation) {}
                    override fun onAnimationEnd(p0: Animation) {
                        if (TutorialActivity.gotHealthAccess && TutorialActivity.gotLocationAccess) {
                            TutorialActivity.finishedTutorial = true
                            startActivity(ProcessedActivity.intent(context))
                            activity?.finish()
                        }
                    }
                    override fun onAnimationStart(p0: Animation) {}
                })
            })
        }

        binding.imageBtnHealthAccessBg.setThrottledOnClickListener {
            Toast.makeText(context, "Will be updated, stay tuned!", Toast.LENGTH_SHORT).show()
            binding.imageBtnHealthAccessText.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out).apply {
                duration = 500
                fillAfter = true
                setAnimationListener(object: Animation.AnimationListener {
                    override fun onAnimationRepeat(p0: Animation) {}
                    override fun onAnimationEnd(p0: Animation) {}
                    override fun onAnimationStart(p0: Animation) {}
                })
            })
            binding.imageBtnHealthAccessBg.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out).apply {
                duration = 500
                fillAfter = true
                setAnimationListener(object: Animation.AnimationListener {
                    override fun onAnimationRepeat(p0: Animation) {}
                    override fun onAnimationEnd(p0: Animation) {
                        TutorialActivity.gotHealthAccess = true
                        if (TutorialActivity.gotLocationAccess) {
                            TutorialActivity.finishedTutorial = true
                            startActivity(ProcessedActivity.intent(context))
                            activity?.finish()
                        }
                    }
                    override fun onAnimationStart(p0: Animation) {}
                })
            })
        }
        binding.imageBtnHealthAccessText.setThrottledOnClickListener {
            Toast.makeText(context, "Will be updated, stay tuned!", Toast.LENGTH_SHORT).show()
            binding.imageBtnHealthAccessText.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out).apply {
                duration = 500
                fillAfter = true
                setAnimationListener(object: Animation.AnimationListener {
                    override fun onAnimationRepeat(p0: Animation) {}
                    override fun onAnimationEnd(p0: Animation) {}
                    override fun onAnimationStart(p0: Animation) {}
                })
            })
            binding.imageBtnHealthAccessBg.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out).apply {
                duration = 500
                fillAfter = true
                setAnimationListener(object: Animation.AnimationListener {
                    override fun onAnimationRepeat(p0: Animation) {}
                    override fun onAnimationEnd(p0: Animation) {
                        TutorialActivity.gotHealthAccess = true
                        if (TutorialActivity.gotLocationAccess) {
                            TutorialActivity.finishedTutorial = true
                            startActivity(ProcessedActivity.intent(context))
                            activity?.finish()
                        }
                    }
                    override fun onAnimationStart(p0: Animation) {}
                })
            })
        }

        binding.imageBtnLocationAccess.setThrottledOnClickListener {
            checkLocationPermission(context)
        }
    }

    private fun checkLocationPermission(context: Context): Boolean {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
            return false
        } else {
            return true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val context = context ?: return
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Success! Permission was granted.
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                        binding.imageBtnLocationAccess.isClickable = false
                        binding.imageBtnLocationAccess.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out).apply {
                            duration = 500
                            fillAfter = true
                            setAnimationListener(object: Animation.AnimationListener {
                                override fun onAnimationRepeat(p0: Animation) {}
                                override fun onAnimationEnd(p0: Animation) {
                                    TutorialActivity.gotLocationAccess = true
                                    if (TutorialActivity.gotHealthAccess) {
                                        TutorialActivity.finishedTutorial = true
                                        startActivity(ProcessedActivity.intent(context))
                                        activity?.finish()
                                    }
                                }
                                override fun onAnimationStart(p0: Animation) {}
                            })
                        })
                    }
                }
            }
        }
    }
}