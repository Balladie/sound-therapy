package com.balladie.soundtherapy.view.ui.tutorial

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.balladie.soundtherapy.R
import com.balladie.soundtherapy.databinding.ActivityTutorialBinding
import com.balladie.soundtherapy.view.setThrottledOnClickListener
import com.balladie.soundtherapy.view.setWindowFullScreen
import com.balladie.soundtherapy.view.ui.base.BaseActivity
import com.balladie.soundtherapy.view.ui.main.MainActivity
import com.balladie.soundtherapy.view.ui.tutorial.adrenaline.AdrenalineFragment
import com.balladie.soundtherapy.view.ui.tutorial.deepsleep.DeepsleepFragment
import com.balladie.soundtherapy.view.ui.tutorial.focus.FocusFragment
import com.balladie.soundtherapy.view.ui.tutorial.healing.HealingFragment
import com.balladie.soundtherapy.view.ui.tutorial.recovery.RecoveryFragment
import kotlinx.android.synthetic.main.activity_tutorial.*
import kotlinx.android.synthetic.main.fragment_adrenaline.*
import kotlinx.android.synthetic.main.fragment_deepsleep.*
import kotlinx.android.synthetic.main.fragment_focus.*
import kotlinx.android.synthetic.main.fragment_healing.*
import kotlinx.android.synthetic.main.fragment_recovery.*
import timber.log.Timber
import javax.inject.Inject

class TutorialActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<TutorialViewModel> { viewModelFactory }

    companion object {
        fun intent(context: Context): Intent =
            Intent(context, TutorialActivity::class.java)

        lateinit var binding: ActivityTutorialBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setWindowFullScreen(window, actionBar)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_tutorial)
        binding.lifecycleOwner = this

        setupViews()
    }

    private fun setupViews() {
        binding.viewPagerTutorial.adapter =
            object : FragmentStateAdapter(supportFragmentManager, lifecycle) {
                val fragments = listOf(
                    AdrenalineFragment.create(),
                    HealingFragment.create(),
                    DeepsleepFragment.create(),
                    FocusFragment.create(),
                    RecoveryFragment.create()
                )

                override fun getItemCount(): Int = fragments.size

                override fun createFragment(position: Int): Fragment = fragments[position]
            }

        binding.dotsIndicatorTutorial.setViewPager2(view_pager_tutorial)
    }
}
