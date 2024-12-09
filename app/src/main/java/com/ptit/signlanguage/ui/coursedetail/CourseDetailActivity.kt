package com.ptit.signlanguage.ui.coursedetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.GridItemDecoration
import com.ptit.signlanguage.databinding.FragmentFullscreenSubjectBinding
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.ui.main.adapter.CourseAdapter
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.view_model.ViewModelFactory
import kotlinx.coroutines.launch

class CourseDetailActivity : AppCompatActivity() {
    private lateinit var mAdapter: CourseAdapter
    private var language = Constants.VI
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
        language = intent.getStringExtra("language") ?: Constants.VI
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
        initObserver()
        initViewBinding()
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

    private fun initViewBinding() {
        if (language.equals(Constants.EN)) {
            mAdapter = CourseAdapter(mutableListOf(), Constants.EN, this)
        } else {
            mAdapter = CourseAdapter(mutableListOf(), Constants.VI, this)
        }
        binding.rvsubject.apply {
            adapter = mAdapter
            layoutManager = GridLayoutManager(this@CourseDetailActivity, 2)
            addItemDecoration(GridItemDecoration(32))
            setHasFixedSize(true)
        }

        binding.imvBack.setOnClickListener { finish() }

    }
}