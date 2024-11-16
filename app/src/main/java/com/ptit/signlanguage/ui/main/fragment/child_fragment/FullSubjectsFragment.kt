package com.ptit.signlanguage.ui.main.fragment.child_fragment

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.GridItemDecoration
import com.ptit.signlanguage.data.prefs.PreferencesHelper
import com.ptit.signlanguage.databinding.FragmentFullscreenSubjectBinding
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.ui.main.adapter.CourseAdapter
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.utils.ObjectLocator
import com.ptit.signlanguage.view_model.ViewModelFactory
import kotlinx.coroutines.launch

class FullSubjectsFragment : AppCompatActivity() {
    private lateinit var mAdapter: CourseAdapter
    private var language = Constants.VI
    private lateinit var prefsHelper: PreferencesHelper
    private val binding by lazy {
        DataBindingUtil.inflate<FragmentFullscreenSubjectBinding>(
            layoutInflater,
            R.layout.fragment_fullscreen_subject,
            null,
            false,
        )
    }
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        window.addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON)
        language = intent.getStringExtra("language") ?: Constants.VI
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, ViewModelFactory)[MainViewModel::class.java]
        initObserver()
        initViewBinding()
        loadPreference()
        viewModel.getSubjectWithProgress()
    }
    private fun initObserver() {
        lifecycleScope.launch {
                viewModel.listSubjectWithProgress.collect{ subjects ->
                    if(subjects.isNotEmpty()){
                        mAdapter.replace(subjects.toMutableList())
                    }
                }
            }
    }
    private fun loadPreference(){
        prefsHelper = PreferencesHelper(binding.root.context)
    }
    private fun initViewBinding() {
        if (language.equals(Constants.EN)) {
            mAdapter = CourseAdapter(mutableListOf(), Constants.EN, this)
        } else {
            mAdapter = CourseAdapter(mutableListOf(), Constants.VI, this)
        }
        mAdapter.addRecentCourse = {
            ObjectLocator.recentCourses.add(it)
        }
        binding.rvsubject.apply {
            adapter = mAdapter
            layoutManager = GridLayoutManager(this@FullSubjectsFragment, 2)
            addItemDecoration(GridItemDecoration(32))
            setHasFixedSize(true)
        }
    }
}
