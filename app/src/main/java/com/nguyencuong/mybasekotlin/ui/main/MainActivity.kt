package com.nguyencuong.mybasekotlin.ui.main

import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.nguyencuong.mybasekotlin.R
import com.nguyencuong.mybasekotlin.databinding.ActivityMainBinding
import com.nguyencuong.mybasekotlin.base.BaseActivity
import com.nguyencuong.mybasekotlin.view_model.ViewModelFactory
import com.nguyencuong.mybasekotlin.utils.Constants
import com.nguyencuong.mybasekotlin.utils.Constants.KEY_PHONE_NUMBER
import com.nguyencuong.mybasekotlin.utils.Status

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