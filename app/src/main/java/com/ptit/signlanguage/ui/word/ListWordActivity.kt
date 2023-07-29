package com.ptit.signlanguage.ui.word

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseActivity
import com.ptit.signlanguage.base.LinearItemDecoration
import com.ptit.signlanguage.databinding.ActivityListWordBinding
import com.ptit.signlanguage.network.model.response.Label
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.ui.main.adapter.LabelAdapter
import com.ptit.signlanguage.ui.score.ActivityScore
import com.ptit.signlanguage.ui.word.adapter.ListWordAdapter
import com.ptit.signlanguage.utils.Constants.VERTICAL
import com.ptit.signlanguage.view_model.ViewModelFactory

class ListWordActivity : BaseActivity<MainViewModel, ActivityListWordBinding>() {
    var adapter: ListWordAdapter = ListWordAdapter(mutableListOf())

    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
    }

    override fun getContentLayout(): Int {
        return R.layout.activity_list_word
    }

    override fun initView() {
        setLightIconStatusBar(false)
        setColorForStatusBar(R.color.color_primary)
        binding.layout.setPadding(0, getStatusBarHeight(this@ListWordActivity), 0, 0)

        binding.rvWord.adapter = adapter
        binding.rvWord.addItemDecoration(LinearItemDecoration(dpToPx(12), VERTICAL))
        adapter.replace(fakeData())
    }

    override fun initListener() {
        binding.btnScore.setOnClickListener {
            val intent = Intent(this@ListWordActivity, ActivityScore::class.java)
            startActivity(intent)
        }
    }

    override fun observerLiveData() {

    }

    private fun fakeData(): MutableList<Label> {
        val listLabel = mutableListOf<Label>()
        for (i in 1..16) {
            var label = Label(i.toString())
            listLabel.add(label)
        }
        return listLabel
    }
}