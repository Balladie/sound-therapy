package com.balladie.soundtherapy.view.ui.tutorial.focus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.balladie.soundtherapy.R
import com.balladie.soundtherapy.databinding.FragmentFocusBinding
import com.balladie.soundtherapy.view.setThrottledOnClickListener
import com.balladie.soundtherapy.view.ui.base.BaseFragment
import com.balladie.soundtherapy.view.ui.tutorial.TutorialActivity

class FocusFragment : BaseFragment() {

    private lateinit var binding: FragmentFocusBinding

    companion object {
        fun create() = FocusFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = DataBindingUtil.inflate<FragmentFocusBinding>(
        inflater, R.layout.fragment_focus, container, false
    ).also { binding = it }.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val context = context ?: return

        setupViews()
    }

    private fun setupViews() {
        binding.imageBtnContinueFocusBg.setThrottledOnClickListener {
            TutorialActivity.binding.viewPagerTutorial.currentItem++
        }
        binding.imageBtnContinueFocusText.setThrottledOnClickListener {
            TutorialActivity.binding.viewPagerTutorial.currentItem++
        }
    }
}