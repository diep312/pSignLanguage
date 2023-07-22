package com.ptit.signlanguage.ui.main.fragment

import androidx.lifecycle.ViewModelProvider
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseFragment
import com.ptit.signlanguage.databinding.FragmentTextToVideoBinding
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.view_model.ViewModelFactory

class TextToVideoFragment : BaseFragment<MainViewModel, FragmentTextToVideoBinding>() {

    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
    }

    override fun getContentLayout(): Int {
        return R.layout.fragment_text_to_video
    }

    override fun observerLiveData() {
        viewModel.apply {

        }
    }

    override fun initView() {

    }

    override fun initListener() {

    }

}