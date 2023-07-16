package com.nguyencuong.mybasekotlin.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import com.nguyencuong.mybasekotlin.R
import com.nguyencuong.mybasekotlin.base.BaseActivity
import com.nguyencuong.mybasekotlin.databinding.ActivityLoginBinding
import com.nguyencuong.mybasekotlin.ui.login.LoginActivity
import com.nguyencuong.mybasekotlin.ui.login.LoginViewModel
import com.nguyencuong.mybasekotlin.view_model.ViewModelFactory

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {
    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[LoginViewModel::class.java]
    }

    override fun getContentLayout(): Int {
        return R.layout.activty_splash
    }

    override fun initView() {
        val intent = Intent(this@SplashActivity, LoginActivity::class.java)
        startActivity(intent)
    }

    override fun initListener() {
    }

    override fun observerLiveData() {
    }
}