package com.balladie.soundtherapy.view.ui.tutorial.recovery

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.balladie.soundtherapy.R
import com.balladie.soundtherapy.databinding.FragmentRecoveryBinding
import com.balladie.soundtherapy.view.setThrottledOnClickListener
import com.balladie.soundtherapy.view.ui.base.BaseFragment
import com.balladie.soundtherapy.view.ui.tutorial.TutorialActivity

class RecoveryFragment : BaseFragment() {

    private lateinit var binding: FragmentRecoveryBinding

    companion object {
        fun create() = RecoveryFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = DataBindingUtil.inflate<FragmentRecoveryBinding>(
        inflater, R.layout.fragment_recovery, container, false
    ).also { binding = it }.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val context = context ?: return

        setupViews(context)
    }

    private fun setupViews(context: Context) {
        binding.imageBtnContinueRecoveryBg.setThrottledOnClickListener {
            TutorialActivity.binding.viewPagerTutorial.currentItem++
        }
        binding.imageBtnContinueRecoveryText.setThrottledOnClickListener {
            TutorialActivity.binding.viewPagerTutorial.currentItem++
        }
    }
}