package com.ptit.signlanguage.ui.topic

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseActivity
import com.ptit.signlanguage.databinding.ActivityTopicBinding
import com.ptit.signlanguage.network.model.response.Lesson
import com.ptit.signlanguage.network.model.response.Subject
import com.ptit.signlanguage.network.model.response.Level
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.ui.topic.adapter.TopicAdapter
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.view_model.ViewModelFactory

class TopicActivity : BaseActivity<MainViewModel, ActivityTopicBinding>() {
    var adapter: TopicAdapter = TopicAdapter(mutableListOf())

    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
    }

    override fun getContentLayout(): Int {
        return R.layout.activity_topic
    }

    override fun initView() {
        setLightIconStatusBar(false)
        setColorForStatusBar(R.color.color_primary)
        binding.layout.setPadding(0, getStatusBarHeight(this@TopicActivity), 0, 0)

        binding.rvTopic.adapter = adapter
        val subject : Subject? = intent.getSerializableExtra(Constants.KEY_SUBJECT) as Subject?
        subject?.let {
            binding.tvNameTopic.text = subject.name
            viewModel.getSubjectAllInfo(it.id)
        }
    }

    override fun initListener() {
        binding.imvBack.setOnClickListener { finish() }
    }

    override fun observerLiveData() {
        viewModel.apply {
            subjectInfoRes.observe(this@TopicActivity) {
                it?.body?.listLevel?.toMutableList()?.let { it1 -> adapter.replace(it1) }
            }
            errorMessage.observe(this@TopicActivity) {
                Toast.makeText(this@TopicActivity, getString(it), Toast.LENGTH_LONG).show()
            }
        }
    }
}