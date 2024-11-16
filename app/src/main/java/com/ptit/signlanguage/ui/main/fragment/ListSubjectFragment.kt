package com.ptit.signlanguage.ui.main.fragment

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseFragment
import com.ptit.signlanguage.base.GridItemDecoration
import com.ptit.signlanguage.base.LinearItemDecoration
import com.ptit.signlanguage.data.prefs.PreferencesHelper
import com.ptit.signlanguage.databinding.FragmentCourseBinding
import com.ptit.signlanguage.network.model.response.Course
import com.ptit.signlanguage.network.model.response.Label
import com.ptit.signlanguage.network.model.response.User
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.ui.main.adapter.CourseAdapter
import com.ptit.signlanguage.ui.main.adapter.LabelAdapter
import com.ptit.signlanguage.ui.main.fragment.child_fragment.FullSubjectsFragment
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.utils.GsonUtils
import com.ptit.signlanguage.utils.ObjectLocator
import com.ptit.signlanguage.view_model.ViewModelFactory
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

class ListSubjectFragment : BaseFragment<MainViewModel, FragmentCourseBinding>() {
    private lateinit var mAdapter: CourseAdapter
    private lateinit var mSavedLabelAdapter: LabelAdapter
    var user : User? = null
    private lateinit var prefsHelper: PreferencesHelper
    private var streaks: Int? = 0
    private var lastSignIn : Long? = 0
    private var listSavedWord = mutableSetOf<Label?>()
    private var listWord = mutableListOf<Label?>()

    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory)[MainViewModel::class.java]
    }
    override fun getContentLayout(): Int {
        return R.layout.fragment_course
    }
    override fun observerLiveData() {
        viewModel.apply {
            lifecycleScope.launch {
                listSubjectWithProgress.collect { subjects ->
                    if (subjects.isNotEmpty()) {
                        mAdapter.replace(subjects.filter {
                            ObjectLocator.recentCourses.contains(it?.id)
                        }.toMutableList())
                    }
                }
            }
            errorMessage.observe(this@ListSubjectFragment) {
                Toast.makeText(this@ListSubjectFragment.requireContext(), getString(it), Toast.LENGTH_LONG).show()
            }
            listLabelRes.observe(this@ListSubjectFragment) { result ->
                if (result?.body != null) {
                    listWord = result.body.toMutableList()
                        .also { it.sortBy { comparator -> comparator!!.id } }
                    listSavedWord = listWord.toMutableSet()
//                    mSavedLabelAdapter.replace(listSavedWord.toMutableList())
                }
            }
            userProgress.observe(this@ListSubjectFragment){
                val roundedScore = it?.totalScore?.roundToInt() ?: 0
                binding.tvScore.text = roundedScore.toString()
                binding.tvSigns.text = it.signs.toString()
            }
        }
    }
    override fun initView() {
        loadPreference()
        mAdapter = if(user?.language.equals(Constants.EN)) {
            CourseAdapter(mutableListOf(), Constants.EN,requireContext())
        } else {
            CourseAdapter(mutableListOf(), Constants.VI, requireContext())
        }
        mSavedLabelAdapter = if(user?.language.equals(Constants.EN)) {
            LabelAdapter(mutableListOf(), Constants.EN, requireContext())
        } else {
            LabelAdapter(mutableListOf(), Constants.VI, requireContext())
        }
        mSavedLabelAdapter.getVideoCallback = { label ->
            callbackFlow {
                trySend(viewModel.getVideoFlow(label))
                awaitClose()
            }
        }
        binding.rvRecentCourses.apply {
            this.adapter = mAdapter
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.addItemDecoration(LinearItemDecoration(50, LinearLayoutManager.HORIZONTAL))
            this.hasFixedSize()
        }
        binding.rvSavedWords.apply{
            adapter = mSavedLabelAdapter
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.addItemDecoration(LinearItemDecoration(16, LinearLayoutManager.VERTICAL))
            this.hasFixedSize()
        }
        binding.tvSeeMoreCourses.setOnClickListener {
            val intent = Intent(activity,FullSubjectsFragment::class.java)
            intent.putExtra("language", user?.language ?: Constants.VI)
            startActivity(intent)
        }
        viewModel.getListLabel()
        viewModel.getUserProgress()
        viewModel.getSubjectWithProgress()
    }

    override fun onStart() {
        super.onStart()
        mAdapter.replace(viewModel.listSubjectWithProgress.value.filter { subject ->
            ObjectLocator.recentCourses.contains(subject?.id)
        }.toMutableList())
        if(mAdapter.listSubject.isEmpty()){
            if (user?.language == Constants.EN) {
                binding.tvNothingShown.text = "No recent courses were taken"
            } else {
                binding.tvNothingShown.text = "Không có khoá học nào gần đây"
            }
            binding.layoutNothing.visibility = View.VISIBLE
            binding.rvRecentCourses.visibility = View.GONE
        }else{
            binding.layoutNothing.visibility = View.GONE
            binding.rvRecentCourses.visibility = View.VISIBLE
        }
        listSavedWord = listWord.filter {
            ObjectLocator.savedLabels.contains(it?.labelEn) || ObjectLocator.savedLabels.contains(
                it?.labelVn
            )
        }.toMutableSet()
        if(listSavedWord.isEmpty()){
            if (user?.language == Constants.EN) {
                binding.tvNothingShown2.text = "No recent words were taken"
            } else {
                binding.tvNothingShown2.text = "Không có tù nào được lưu"
            }
            binding.layoutNothing2.visibility = View.VISIBLE
            binding.rvSavedWords.visibility = View.GONE
        }else{
            binding.layoutNothing2.visibility = View.GONE
            binding.rvSavedWords.visibility = View.VISIBLE
            mSavedLabelAdapter.replace(listSavedWord.toMutableList())
        }
    }
    override fun initListener() {

    }
    private fun loadPreference()
    {
        prefsHelper = PreferencesHelper(binding.root.context)
        val userJson = prefsHelper.getString(Constants.KEY_PREF_DATA_LOGIN)
        user = GsonUtils.deserialize(userJson, User::class.java)
        lastSignIn = prefsHelper.getLong("lastSignIn")
        if(lastSignIn == -1L){
            streaks = 1
            prefsHelper.save("lastSignIn", System.currentTimeMillis())
        }else{
            prefsHelper.getInt("streaks").let {
                streaks = it
                if(System.currentTimeMillis() - lastSignIn!! > 86400000){
                    streaks = (streaks ?: 0) + 1
                    prefsHelper.save("lastSignIn", System.currentTimeMillis())
                }
            }
        }
        streaks?.let { prefsHelper.save("streaks", it) }
        streaks?.let {
            binding.tvStreaks.text = streaks.toString()
        }
    }

    data class Color(
        val background: Int,
        val overlay: Int,
        val progressbar: Int
    )

}