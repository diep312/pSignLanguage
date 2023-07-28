package com.ptit.signlanguage.ui.main.fragment

import androidx.lifecycle.ViewModelProvider
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseFragment
import com.ptit.signlanguage.databinding.FragmentTextToVideoBinding
import com.ptit.signlanguage.network.model.response.Label
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.ui.main.adapter.LabelAdapter
import com.ptit.signlanguage.view_model.ViewModelFactory

class TextToVideoFragment : BaseFragment<MainViewModel, FragmentTextToVideoBinding>() {
    var adapter: LabelAdapter = LabelAdapter(mutableListOf())

    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
    }

    override fun getContentLayout(): Int {
        return R.layout.fragment_text_to_video
    }

    override fun observerLiveData() {
        viewModel.apply {

        }
    }

    override fun initView() {
        binding.rvLabel.adapter = adapter
//        binding.rvLabel.addItemDecoration(LinearItemDecoration(dpToPx(12), VERTICAL))

        adapter.replace(fakeData())
    }

    override fun initListener() {

    }

    fun fakeData(): MutableList<Label> {
        val listLabel = mutableListOf<Label>()
        for (i in 1..16) {
            var label = Label(i.toString())
            listLabel.add(label)
        }
        return listLabel
    }

}