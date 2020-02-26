package com.balladie.soundtherapy.view.ui.tutorial.healing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.balladie.soundtherapy.R
import com.balladie.soundtherapy.databinding.FragmentHealingBinding
import com.balladie.soundtherapy.view.setThrottledOnClickListener
import com.balladie.soundtherapy.view.ui.base.BaseFragment
import com.balladie.soundtherapy.view.ui.tutorial.TutorialActivity

class HealingFragment : BaseFragment() {

    private lateinit var binding: FragmentHealingBinding

    companion object {
        fun create() = HealingFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = DataBindingUtil.inflate<FragmentHealingBinding>(
        inflater, R.layout.fragment_healing, container, false
    ).also { binding = it }.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val context = context ?: return

        setupViews()
    }

    private fun setupViews() {
        binding.imageBtnContinueHealing.setThrottledOnClickListener {
            TutorialActivity.binding.viewPagerTutorial.currentItem++
        }
    }
}