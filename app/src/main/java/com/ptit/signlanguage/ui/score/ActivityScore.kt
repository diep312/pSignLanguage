package com.ptit.signlanguage.ui.score

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseActivity
import com.ptit.signlanguage.base.GridThreeColumnDecoration
import com.ptit.signlanguage.databinding.ActivityScoreBinding
import com.ptit.signlanguage.network.model.response.Subject
import com.ptit.signlanguage.network.model.response.Token
import com.ptit.signlanguage.network.model.response.User
import com.ptit.signlanguage.network.model.response.subjectWrap.Level
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.ui.score.adapter.UserScoreAdapter
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.view_model.ViewModelFactory

class ActivityScore : BaseActivity<MainViewModel, ActivityScoreBinding>() {
    var adapter: UserScoreAdapter = UserScoreAdapter(mutableListOf())
    var subject: Subject? = null
    var level: Level? = null

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

        subject = intent.getSerializableExtra(Constants.KEY_SUBJECT) as Subject?
        level = intent.getSerializableExtra(Constants.KEY_LEVEL) as Level?

        if(level?.levelId != null && subject?.id != null) {
            viewModel.getScoreWithSubject(level?.levelId!!, subject?.id!!)
        }
    }

    override fun initListener() {
        binding.btnBack.setOnClickListener { finish() }
    }

    override fun observerLiveData() {
        viewModel.apply {
            scoreWithSubject.observe(this@ActivityScore) {
                it?.body?.scoreList?.toMutableList()?.let { scores -> adapter.replace(scores) }
            }
        }
    }
}