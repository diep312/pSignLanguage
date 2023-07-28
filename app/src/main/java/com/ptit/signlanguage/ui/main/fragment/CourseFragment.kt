package com.ptit.signlanguage.ui.main.fragment

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseFragment
import com.ptit.signlanguage.base.GridItemDecoration
import com.ptit.signlanguage.databinding.FragmentCourseBinding
import com.ptit.signlanguage.network.model.response.Course
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.ui.main.adapter.CourseAdapter
import com.ptit.signlanguage.view_model.ViewModelFactory

class CourseFragment : BaseFragment<MainViewModel, FragmentCourseBinding>() {
    var adapter: CourseAdapter = CourseAdapter(mutableListOf())

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
        val gridLayoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        binding.rvCourse.layoutManager = gridLayoutManager
        binding.rvCourse.adapter = adapter
        binding.rvCourse.setHasFixedSize(true)
        binding.rvCourse.addItemDecoration(GridItemDecoration(dpToPx(12)))

        adapter.replace(fakeData())
    }

    override fun initListener() {

    }

    fun fakeData(): MutableList<Course> {
        val listCourse = mutableListOf<Course>()
        for (i in 1..16) {
            var label = Course(i.toString())
            listCourse.add(label)
        }
        return listCourse
    }
}