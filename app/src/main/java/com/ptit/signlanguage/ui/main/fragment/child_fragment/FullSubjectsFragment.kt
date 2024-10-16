package com.ptit.signlanguage.ui.main.fragment.child_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.GridItemDecoration
import com.ptit.signlanguage.databinding.FragmentFullscreenSubjectBinding
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.ui.main.adapter.CourseAdapter
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.view_model.ViewModelFactory

class FullSubjectsFragment : Fragment() {
    var language: String = Constants.EN
    private lateinit var mAdapter: CourseAdapter
    private val binding by lazy {
        DataBindingUtil.inflate<FragmentFullscreenSubjectBinding>(
            layoutInflater,
            R.layout.fragment_fullscreen_subject,
            null,
            false,
        )
    }
    private lateinit var viewModel: MainViewModel

    companion object {
        fun newInstance(language: String) =
            FullSubjectsFragment().apply {
                this.language = language
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = binding.root

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
        initObserver()
        initViewBinding()
        viewModel.getListSubject()
    }

    private fun initObserver() {
        viewModel.listSubjectRes.observe(viewLifecycleOwner) {
            if (it?.body != null) {

            }
        }
    }

    private fun initViewBinding() {
        if (language.equals(Constants.EN)) {
            mAdapter = CourseAdapter(mutableListOf(), Constants.EN, requireContext())
        } else {
            mAdapter = CourseAdapter(mutableListOf(), Constants.VI, requireContext())
        }
        binding.rvsubject.apply {
            adapter = mAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            addItemDecoration(GridItemDecoration(2))
            setHasFixedSize(true)
        }
    }
}
