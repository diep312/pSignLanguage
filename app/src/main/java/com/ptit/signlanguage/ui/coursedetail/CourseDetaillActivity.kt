package com.ptit.signlanguage.ui.coursedetail
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseActivity
import com.ptit.signlanguage.base.GridItemDecoration
import com.ptit.signlanguage.data.prefs.PreferencesHelper
import com.ptit.signlanguage.databinding.ActivityCourseFullViewBinding
import com.ptit.signlanguage.network.model.response.User
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.ui.main.adapter.CourseAdapter
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.utils.GsonUtils
import com.ptit.signlanguage.view_model.ViewModelFactory

public class CourseDetaillActivity : BaseActivity<MainViewModel, ActivityCourseFullViewBinding>(){
    lateinit var adapter: CourseAdapter
    var user : User? = null
    private lateinit var prefsHelper: PreferencesHelper

    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
    }

    override fun getContentLayout(): Int {
        return R.layout.activity_course_full_view;
    }

    override fun observerLiveData() {
        viewModel.apply {
            listSubjectRes.observe(this@CourseDetaillActivity) {
                if(it?.body != null) {
                    val list = it.body.toMutableList().apply {
                        sortBy { it!!.id }
                    }
                    adapter.replace(list)
                }
            }
            errorMessage.observe(this@CourseDetaillActivity) {
                Toast.makeText(this@CourseDetaillActivity, getString(it), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun initView() {
        setLightIconStatusBar(true)
        setColorForStatusBar(R.color.color_bg)
        prefsHelper = PreferencesHelper(binding.root.context)
        val userJson = prefsHelper.getString(Constants.KEY_PREF_DATA_LOGIN)
        user = GsonUtils.deserialize(userJson, User::class.java)

        adapter = if(user?.language.equals(Constants.EN)) {
            CourseAdapter(mutableListOf(), Constants.EN, this)
        } else {
            CourseAdapter(mutableListOf(), Constants.VI, this)
        }

        val gridLayoutManager = GridLayoutManager(binding.root.context, 2, GridLayoutManager.VERTICAL, false)
        binding.rvCourse.layoutManager = gridLayoutManager
        binding.rvCourse.adapter = adapter
        binding.rvCourse.setHasFixedSize(true)
        binding.rvCourse.addItemDecoration(GridItemDecoration(dpToPx(25)))

        binding.imvBack.setOnClickListener { finish() }
        viewModel.getListSubject()
    }

    override fun initListener() {

    }

}
