package com.ptit.signlanguage.ui.label

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseActivity
import com.ptit.signlanguage.base.LinearItemDecoration
import com.ptit.signlanguage.data.prefs.PreferencesHelper
import com.ptit.signlanguage.databinding.ActivityListLabelBinding
import com.ptit.signlanguage.network.model.response.Label
import com.ptit.signlanguage.network.model.response.Subject
import com.ptit.signlanguage.network.model.response.User
import com.ptit.signlanguage.network.model.response.subjectWrap.Level
import com.ptit.signlanguage.ui.label.adapter.ListLabelAdapter
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.ui.score.ActivityScore
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.utils.Constants.VERTICAL
import com.ptit.signlanguage.utils.GsonUtils
import com.ptit.signlanguage.view_model.ViewModelFactory

class ListLabelActivity : BaseActivity<MainViewModel, ActivityListLabelBinding>() {
    lateinit var adapter: ListLabelAdapter
    private lateinit var prefsHelper: PreferencesHelper
    var user: User? = null
    var subject: Subject? = null
    var level: Level? = null

    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
    }

    override fun getContentLayout(): Int {
        return R.layout.activity_list_label
    }

    override fun initView() {
        setLightIconStatusBar(false)
        setColorForStatusBar(R.color.color_primary)
        binding.layout.setPadding(0, getStatusBarHeight(this@ListLabelActivity), 0, 0)

        prefsHelper = PreferencesHelper(binding.root.context)
        val userJson = prefsHelper.getString(Constants.KEY_PREF_DATA_LOGIN)
        user = GsonUtils.deserialize(userJson, User::class.java)

        subject = intent.getSerializableExtra(Constants.KEY_SUBJECT) as Subject?
        level = intent.getSerializableExtra(Constants.KEY_LEVEL) as Level?

        binding.tvRank.text = getString(R.string.str_level, level?.levelId.toString())
        if (user?.language.equals(Constants.EN)) {
            binding.tvNameTopic.text = subject?.name_en
            adapter = ListLabelAdapter(mutableListOf(), Constants.EN)
        } else {
            binding.tvNameTopic.text = subject?.name
            adapter = ListLabelAdapter(mutableListOf(), Constants.VI)
        }

        binding.rvWord.adapter = adapter
        binding.rvWord.addItemDecoration(LinearItemDecoration(dpToPx(12), VERTICAL))
        level?.listLabel?.toMutableList()?.let { adapter.replace(it) }
    }

    override fun initListener() {
        binding.btnScore.setOnClickListener {
            val intent = Intent(this@ListLabelActivity, ActivityScore::class.java)
            intent.putExtra(Constants.KEY_LEVEL, level)
            intent.putExtra(Constants.KEY_SUBJECT, subject)
            startActivity(intent)
        }
        binding.imvBack.setOnClickListener { finish() }
    }

    override fun observerLiveData() {

    }

    private fun fakeData(): MutableList<Label> {
        val listLabel = mutableListOf<Label>()
        for (i in 1..16) {
            var label = Label()
            listLabel.add(label)
        }
        return listLabel
    }
}