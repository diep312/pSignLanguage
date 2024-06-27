package com.ptit.signlanguage.ui.predict.fragment

import androidx.lifecycle.ViewModelProvider
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseFragment
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.databinding.Fragment1Binding
import com.ptit.signlanguage.databinding.Fragment3Binding
import com.ptit.signlanguage.view_model.ViewModelFactory


class Fragment1 : BaseFragment<MainViewModel, Fragment3Binding>() {
    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
    }

    override fun getContentLayout(): Int = R.layout.fragment1

    override fun observerLiveData() {
    }

    override fun initView() {
    }

    override fun initListener() {
    }
}