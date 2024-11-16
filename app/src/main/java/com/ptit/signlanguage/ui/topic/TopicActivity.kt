package com.ptit.signlanguage.ui.topic

import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseActivity
import com.ptit.signlanguage.data.prefs.PreferencesHelper
import com.ptit.signlanguage.databinding.ActivityTopicBinding
import com.ptit.signlanguage.network.model.response.Subject
import com.ptit.signlanguage.network.model.response.User
import com.ptit.signlanguage.network.model.response.subjectWrap.Level
import com.ptit.signlanguage.ui.label.ListLabelActivity
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.ui.topic.adapter.TopicAdapter
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.utils.Constants.EMPTY_STRING
import com.ptit.signlanguage.utils.GsonUtils
import com.ptit.signlanguage.view_model.ViewModelFactory

class TopicActivity : BaseActivity<MainViewModel, ActivityTopicBinding>(), TopicAdapter.CallbackTopic {
    lateinit var adapter: TopicAdapter
    private lateinit var prefsHelper: PreferencesHelper
    var user: User? = null
    var subject: Subject? = null
    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
    }

    override fun getContentLayout(): Int {
        return R.layout.activity_topic
    }

    override fun initView() {
        setLightIconStatusBar(true)
        setColorForStatusBar(R.color.color_bg)
        binding.layout.setPadding(0, getStatusBarHeight(this@TopicActivity), 0, 0)

        prefsHelper = PreferencesHelper(binding.root.context)
        val userJson = prefsHelper.getString(Constants.KEY_PREF_DATA_LOGIN)
        user = GsonUtils.deserialize(userJson, User::class.java)

        adapter = if (user?.language.equals(Constants.EN)) {
            TopicAdapter(mutableListOf(), Constants.EN, this)
        } else {
            TopicAdapter(mutableListOf(), Constants.VI, this)
        }

        binding.progressBar.max = subject?.totalLabels ?: 0
        binding.progressBar.progress = subject?.learnedLabels ?: 0
        binding.tvProgressPercentage.text  = "${subject?.learnedLabels}/"+"${subject?.totalLabels}"

        binding.rvTopic.adapter = adapter
        subject = intent.getSerializableExtra(Constants.KEY_SUBJECT) as Subject?
        subject?.let {
            if (user?.language.equals(Constants.EN)) {
                binding.tvNameTopic.text = subject?.name_en ?: EMPTY_STRING
            } else {
                binding.tvNameTopic.text = subject?.name ?: EMPTY_STRING
            }
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

    override fun onClickTopic(level: Level?) {
        if(!isDoubleClick()) {
            val intent = Intent(binding.root.context, ListLabelActivity::class.java)
            intent.putExtra(Constants.KEY_LEVEL, level)
            intent.putExtra(Constants.KEY_SUBJECT, subject)
            binding.root.context.startActivity(intent)
        }
    }

    override fun onRestart() {
        super.onRestart()
        viewModel.apply {
            getSubjectAllInfo(subject?.id ?: 0)
        }
    }
}


