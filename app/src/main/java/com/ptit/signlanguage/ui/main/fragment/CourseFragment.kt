package com.ptit.signlanguage.ui.main.fragment

import androidx.lifecycle.ViewModelProvider
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseFragment
import com.ptit.signlanguage.databinding.FragmentCourseBinding
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.view_model.ViewModelFactory

class CourseFragment : BaseFragment<MainViewModel, FragmentCourseBinding>() {

    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
    }

    override fun getContentLayout(): Int {
        return R.layout.fragment_course
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