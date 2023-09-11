package com.ptit.signlanguage.ui.main

import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseActivity
import com.ptit.signlanguage.databinding.ActivityMainBinding
import com.ptit.signlanguage.ui.main.adapter.MainViewPagerAdapter
import com.ptit.signlanguage.utils.Constants.PAGE_0
import com.ptit.signlanguage.utils.Constants.PAGE_1
import com.ptit.signlanguage.utils.Constants.PAGE_2
import com.ptit.signlanguage.utils.Constants.PAGE_3
import com.ptit.signlanguage.utils.Constants.PAGE_4
import com.ptit.signlanguage.utils.Constants.TOTAL_SIZE_PAGE
import com.ptit.signlanguage.view_model.ViewModelFactory

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    lateinit var myViewPagerAdapter : MainViewPagerAdapter
    private var backPressedTime: Long = 0

    override fun getContentLayout(): Int {
        return R.layout.activity_main
    }

    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
    }

    override fun initView() {
        setLightIconStatusBar(false)
        setColorForStatusBar(R.color.color_primary)
        binding.layout.setPadding(0, getStatusBarHeight(this@MainActivity), 0, 0)

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

}