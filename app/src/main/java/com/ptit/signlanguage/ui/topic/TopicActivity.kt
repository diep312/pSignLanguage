package com.ptit.signlanguage.ui.topic

import androidx.lifecycle.ViewModelProvider
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseActivity
import com.ptit.signlanguage.databinding.ActivityTopicBinding
import com.ptit.signlanguage.network.model.response.Label
import com.ptit.signlanguage.network.model.response.Lesson
import com.ptit.signlanguage.network.model.response.Topic
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.ui.main.adapter.LabelAdapter
import com.ptit.signlanguage.ui.topic.adapter.TopicAdapter
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
        adapter.replace(fakeData())
    }

    override fun initListener() {

    }

    override fun observerLiveData() {

    }

    private fun fakeData(): MutableList<Topic> {
        val listTopic = mutableListOf<Topic>()
        for(t in 1 ..3) {
            val topic = Topic()
            for (l in 1..6) {
                val lesson = Lesson()
                topic.listLesson.add(lesson)
            }
            listTopic.add(topic)
        }
        return listTopic
    }
}