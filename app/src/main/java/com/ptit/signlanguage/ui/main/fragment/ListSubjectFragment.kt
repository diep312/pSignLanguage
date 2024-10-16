package com.ptit.signlanguage.ui.main.fragment

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseFragment
import com.ptit.signlanguage.base.GridItemDecoration
import com.ptit.signlanguage.base.LinearItemDecoration
import com.ptit.signlanguage.data.prefs.PreferencesHelper
import com.ptit.signlanguage.databinding.FragmentCourseBinding
import com.ptit.signlanguage.network.model.response.Course
import com.ptit.signlanguage.network.model.response.Label
import com.ptit.signlanguage.network.model.response.User
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.ui.main.adapter.CourseAdapter
import com.ptit.signlanguage.ui.main.adapter.LabelAdapter
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.utils.GsonUtils
import com.ptit.signlanguage.view_model.ViewModelFactory
import kotlin.random.Random

class ListSubjectFragment : BaseFragment<MainViewModel, FragmentCourseBinding>() {
    private lateinit var mAdapter: CourseAdapter
    private lateinit var mSavedLabelAdapter: LabelAdapter
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
                    val list = it.body.toMutableList().apply {
                        sortBy { it!!.id }
                    }
                    mAdapter.replace(list.subList(0,3))
                }
            }
            errorMessage.observe(this@ListSubjectFragment) {
                Toast.makeText(this@ListSubjectFragment.requireContext(), getString(it), Toast.LENGTH_LONG).show()
            }
            listLabelRes.observe(this@ListSubjectFragment){ result ->
                if(result?.body != null) {
                    val list = result.body.toMutableList().also { it.sortBy { comparator -> comparator!!.id } }
                    // Assume recent labels fetch API
                    mSavedLabelAdapter.replace(list.subList(5, 17).also { it.add(Label(isShow = false)) })
                }
            }
        }
    }
    override fun initView() {
        prefsHelper = PreferencesHelper(binding.root.context)
        val userJson = prefsHelper.getString(Constants.KEY_PREF_DATA_LOGIN)
        user = GsonUtils.deserialize(userJson, User::class.java)

        mAdapter = if(user?.language.equals(Constants.EN)) {
            CourseAdapter(mutableListOf(), Constants.EN,requireContext())
        } else {
            CourseAdapter(mutableListOf(), Constants.VI, requireContext())
        }
        mSavedLabelAdapter = if(user?.language.equals(Constants.EN)) {
            LabelAdapter(mutableListOf(), Constants.EN)
        } else {
            LabelAdapter(mutableListOf(), Constants.VI)
        }
        binding.rvRecentCourses.apply {
            this.adapter = mAdapter
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.addItemDecoration(LinearItemDecoration(50, LinearLayoutManager.HORIZONTAL))
            this.hasFixedSize()
        }
        binding.rvSavedWords.apply{
            adapter = mSavedLabelAdapter
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.addItemDecoration(LinearItemDecoration(30, LinearLayoutManager.VERTICAL))
            this.hasFixedSize()
        }
        viewModel.getListSubject()
        viewModel.getListLabel()

    }

    override fun initListener() {

    }
    data class Color(
        val background: Int,
        val overlay: Int,
        val progressbar: Int
    )

}