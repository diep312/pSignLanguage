package com.ptit.signlanguage.ui.main.fragment.child_fragment

import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
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

class FullSubjectsFragment : AppCompatActivity() {
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
        viewModel.listSubjectRes.observe(this) { data ->
            if (data?.body != null) {
                val list = data.body.toMutableList().apply {
                    sortBy { it!!.id }
                }
                mAdapter.replace(list)
            }
        }
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
            layoutManager = GridLayoutManager(this@FullSubjectsFragment, 2)
            addItemDecoration(GridItemDecoration(32))
            setHasFixedSize(true)
        }
    }
}
