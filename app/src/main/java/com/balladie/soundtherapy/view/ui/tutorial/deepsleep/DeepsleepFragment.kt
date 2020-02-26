package com.balladie.soundtherapy.view.ui.tutorial.deepsleep

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.balladie.soundtherapy.R
import com.balladie.soundtherapy.databinding.FragmentDeepsleepBinding
import com.balladie.soundtherapy.view.setThrottledOnClickListener
import com.balladie.soundtherapy.view.ui.base.BaseFragment
import com.balladie.soundtherapy.view.ui.tutorial.TutorialActivity

class DeepsleepFragment : BaseFragment() {

    private lateinit var binding: FragmentDeepsleepBinding

    companion object {
        fun create() = DeepsleepFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = DataBindingUtil.inflate<FragmentDeepsleepBinding>(
        inflater, R.layout.fragment_deepsleep, container, false
    ).also { binding = it }.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val context = context ?: return

        setupViews()
    }

    private fun setupViews() {
        binding.imageBtnContinueDeepsleep.setThrottledOnClickListener {
            TutorialActivity.binding.viewPagerTutorial.currentItem++
        }
    }
}