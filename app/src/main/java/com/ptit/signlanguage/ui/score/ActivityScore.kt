package com.ptit.signlanguage.ui.score

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseActivity
import com.ptit.signlanguage.base.GridThreeColumnDecoration
import com.ptit.signlanguage.databinding.ActivityScoreBinding
import com.ptit.signlanguage.network.model.response.User
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.ui.score.adapter.UserScoreAdapter
import com.ptit.signlanguage.view_model.ViewModelFactory

class ActivityScore : BaseActivity<MainViewModel, ActivityScoreBinding>() {
    var adapter: UserScoreAdapter = UserScoreAdapter(mutableListOf())
    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
    }

    override fun getContentLayout(): Int {
        return R.layout.activity_score
    }

    override fun initView() {
        setLightIconStatusBar(false)
        setColorForStatusBar(R.color.color_primary)
        binding.layout.setPadding(0, getStatusBarHeight(this@ActivityScore), 0, 0)

        val gridLayoutManager = object : GridLayoutManager(binding.root.context, 3, GridLayoutManager.VERTICAL, false) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        binding.rvUser.layoutManager = gridLayoutManager
        binding.rvUser.adapter = adapter
        binding.rvUser.setHasFixedSize(true)
        binding.rvUser.addItemDecoration(GridThreeColumnDecoration(3, dpToPx(12), true))
        adapter.replace(fakeData())
    }

    override fun initListener() {
        binding.btnBack.setOnClickListener { finish() }
    }

    override fun observerLiveData() {

    }

    private fun fakeData(): MutableList<User> {
        val listTopic = mutableListOf<User>()
        for (t in 1..6) {
            val user = User("", "", "", "")
            listTopic.add(user)
        }
        return listTopic
    }
}