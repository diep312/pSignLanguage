package com.ptit.signlanguage.ui.predict.fragment

import androidx.lifecycle.ViewModelProvider
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseFragment
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.databinding.Fragment2Binding
import com.ptit.signlanguage.view_model.ViewModelFactory


class Fragment2 : BaseFragment<MainViewModel, Fragment2Binding>() {
    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
    }

    override fun getContentLayout(): Int = R.layout.fragment2

    override fun observerLiveData() {
    }

    override fun initView() {
    }

    override fun initListener() {
    }
}