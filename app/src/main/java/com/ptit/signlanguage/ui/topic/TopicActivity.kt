package com.ptit.signlanguage.ui.topic

import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseActivity
import com.ptit.signlanguage.data.prefs.PreferencesHelper
import com.ptit.signlanguage.databinding.ActivityTopicBinding
import com.ptit.signlanguage.network.model.response.Subject
import com.ptit.signlanguage.network.model.response.User
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.ui.topic.adapter.TopicAdapter
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.utils.GsonUtils
import com.ptit.signlanguage.view_model.ViewModelFactory

class TopicActivity : BaseActivity<MainViewModel, ActivityTopicBinding>() {
    lateinit var adapter: TopicAdapter
    private lateinit var prefsHelper: PreferencesHelper
    var user: User? = null
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

        prefsHelper = PreferencesHelper(binding.root.context)
        val userJson = prefsHelper.getString(Constants.KEY_PREF_DATA_LOGIN)
        user = GsonUtils.deserialize(userJson, User::class.java)

        adapter = if (user?.language.equals(Constants.EN)) {
            TopicAdapter(mutableListOf(), Constants.EN)
        } else {
            TopicAdapter(mutableListOf(), Constants.VI)
        }
        binding.rvTopic.adapter = adapter
        val subject: Subject? = intent.getSerializableExtra(Constants.KEY_SUBJECT) as Subject?
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