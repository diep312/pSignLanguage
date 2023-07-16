package com.ptit.signlanguage.ui.main

import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.ptit.signlanguage.R
import com.ptit.signlanguage.databinding.ActivityMainBinding
import com.ptit.signlanguage.base.BaseActivity
import com.ptit.signlanguage.view_model.ViewModelFactory
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.utils.Constants.KEY_PHONE_NUMBER
import com.ptit.signlanguage.utils.Status

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    override fun getContentLayout(): Int {
        return R.layout.activity_main
    }

    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
    }

    override fun initView() {
//        viewModel.showLoading()
//        viewModel.getUsers()

        setLightIconStatusBar(true)

        binding.tv.text = intent.getStringExtra(KEY_PHONE_NUMBER)
    }

    override fun initListener() {
    }

    override fun observerLiveData() {
        viewModel.apply {
            viewModel.listUser.observe(this@MainActivity) {
                viewModel.hideLoading()
                when (it.status) {
                    Status.SUCCESS -> {
                        Log.d(Constants.TAG_SUCCESS, it.data.toString())
                    }
                    Status.ERROR -> {
                        Log.e(Constants.TAG_ERROR, it.message.toString())
                    }
                    Status.LOADING -> {
                        Log.d(Constants.TAG_LOADING, "LOADING")
                        it.data.toString()
                    }
                }
            }
        }
    }


}