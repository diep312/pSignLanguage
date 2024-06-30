package com.ptit.signlanguage.ui.main.fragment

import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseFragment
import com.ptit.signlanguage.base.GridItemDecoration
import com.ptit.signlanguage.data.prefs.PreferencesHelper
import com.ptit.signlanguage.databinding.FragmentCourseBinding
import com.ptit.signlanguage.network.model.response.Course
import com.ptit.signlanguage.network.model.response.User
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.ui.main.adapter.CourseAdapter
import com.ptit.signlanguage.ui.main.adapter.LabelAdapter
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.utils.GsonUtils
import com.ptit.signlanguage.view_model.ViewModelFactory

class ListSubjectFragment : BaseFragment<MainViewModel, FragmentCourseBinding>() {
    lateinit var adapter: CourseAdapter
    var user : User? = null
    private lateinit var prefsHelper: PreferencesHelper

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
        prefsHelper = PreferencesHelper(binding.root.context)
        val userJson = prefsHelper.getString(Constants.KEY_PREF_DATA_LOGIN)
        user = GsonUtils.deserialize(userJson, User::class.java)

        adapter = if(user?.language.equals(Constants.EN)) {
            CourseAdapter(mutableListOf(), Constants.EN)
        } else {
            CourseAdapter(mutableListOf(), Constants.VI)
        }

        val gridLayoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        binding.rvCourse.layoutManager = gridLayoutManager
        binding.rvCourse.adapter = adapter
        binding.rvCourse.setHasFixedSize(true)
        binding.rvCourse.addItemDecoration(GridItemDecoration(dpToPx(20)))
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