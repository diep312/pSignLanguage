package com.ptit.signlanguage.ui.main.fragment

import android.widget.Toast
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

class ListSubjectFragment : BaseFragment<MainViewModel, FragmentCourseBinding>() {
    var adapter: CourseAdapter = CourseAdapter(mutableListOf())

    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
    }

    override fun getContentLayout(): Int {
        return R.layout.fragment_course
    }

    override fun observerLiveData() {
        viewModel.apply {
            listSubjectRes.observe(this@ListSubjectFragment) {
                if(it?.body != null) {
                    adapter.replace(it.body.toMutableList())
                }
            }
            errorMessage.observe(this@ListSubjectFragment) {
                Toast.makeText(this@ListSubjectFragment.requireContext(), getString(it), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun initView() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        binding.rvCourse.layoutManager = gridLayoutManager
        binding.rvCourse.adapter = adapter
        binding.rvCourse.setHasFixedSize(true)
        binding.rvCourse.addItemDecoration(GridItemDecoration(dpToPx(12)))

        viewModel.getListSubject()
//        adapter.replace(fakeData())
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