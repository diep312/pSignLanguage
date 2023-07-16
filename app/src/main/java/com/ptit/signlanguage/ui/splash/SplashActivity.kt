package com.ptit.signlanguage.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseActivity
import com.ptit.signlanguage.databinding.ActivityLoginBinding
import com.ptit.signlanguage.ui.login.LoginActivity
import com.ptit.signlanguage.ui.login.LoginViewModel
import com.ptit.signlanguage.view_model.ViewModelFactory

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