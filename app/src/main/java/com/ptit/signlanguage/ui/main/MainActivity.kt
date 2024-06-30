package com.ptit.signlanguage.ui.main

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseActivity
import com.ptit.signlanguage.data.prefs.PreferencesHelper
import com.ptit.signlanguage.databinding.ActivityMainBinding
import com.ptit.signlanguage.network.model.response.User
import com.ptit.signlanguage.ui.main.adapter.MainViewPagerAdapter
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.utils.Constants.PAGE_0
import com.ptit.signlanguage.utils.Constants.PAGE_1
import com.ptit.signlanguage.utils.Constants.PAGE_2
import com.ptit.signlanguage.utils.Constants.PAGE_3
import com.ptit.signlanguage.utils.GsonUtils
import com.ptit.signlanguage.view_model.ViewModelFactory
import java.util.*


class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    lateinit var myViewPagerAdapter : MainViewPagerAdapter
    private var backPressedTime: Long = 0
    private lateinit var prefsHelper: PreferencesHelper
    var user: User? = null

    override fun getContentLayout(): Int {
        return R.layout.activity_main
    }

    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
    }

    override fun initView() {
        setLightIconStatusBar(true)
        setColorForStatusBar(R.color.color_bg)
        verifyStoragePermissions(this)
        binding.layout.setPadding(0, getStatusBarHeight(this@MainActivity) - 20, 0, 0)

        prefsHelper = PreferencesHelper(binding.root.context)
        val userJson = prefsHelper.getString(Constants.KEY_PREF_DATA_LOGIN)
        user = GsonUtils.deserialize(userJson, User::class.java)
        // config language
        changeLanguage()

        myViewPagerAdapter = MainViewPagerAdapter(this)
        binding.vp.offscreenPageLimit = 4
        binding.vp.adapter = myViewPagerAdapter
        binding.vp.isUserInputEnabled = false
    }

    override fun initListener() {
        binding.bnvMenu.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btn_video_to_text -> {
                    binding.vp.currentItem = PAGE_0
                }
                R.id.btn_text_to_video -> {
                    binding.vp.currentItem = PAGE_1
                }
                R.id.btn_course -> {
                    binding.vp.currentItem = PAGE_2
                }
                R.id.btn_account -> {
                    binding.vp.currentItem = PAGE_3
                }
            }
            true
        }

        binding.vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    PAGE_0 -> {
                        binding.bnvMenu.menu.findItem(R.id.btn_video_to_text).isChecked = true
                    }
                    PAGE_1 -> {
                        binding.bnvMenu.menu.findItem(R.id.btn_text_to_video).isChecked = true
                    }
                    PAGE_2 -> {
                        binding.bnvMenu.menu.findItem(R.id.btn_course).isChecked = true
                    }
                    PAGE_3 -> {
                        binding.bnvMenu.menu.findItem(R.id.btn_account).isChecked = true
                    }
                }
            }
        })

        if(binding.vp.currentItem == PAGE_0){
            setLightIconStatusBar(false)
            setColorForStatusBar(R.color.color_primary)
        }
        else{
            setLightIconStatusBar(true)
            setColorForStatusBar(R.color.color_bg)
        }

    }

    override fun observerLiveData() {
        viewModel.apply {

        }
    }

    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finishAffinity()
        } else {
            Toast.makeText(
                this@MainActivity,
                getString(R.string.str_back_press),
                Toast.LENGTH_SHORT
            ).show()
        }
        backPressedTime = System.currentTimeMillis()
    }

    private fun changeLanguage() {
        val resources: Resources = this@MainActivity.resources
        val configuration: Configuration = resources.configuration

        val locale = Locale(if(user?.language.equals("EN")) "en" else "vi")
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf<String>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private fun verifyStoragePermissions(activity: Activity?) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }
}