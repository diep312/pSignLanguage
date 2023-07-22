package com.ptit.signlanguage.ui.login

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseActivity
import com.ptit.signlanguage.databinding.ActivityLoginBinding
import com.ptit.signlanguage.ui.main.MainActivity
import com.ptit.signlanguage.ui.register.RegisterActivity
import com.ptit.signlanguage.view_model.ViewModelFactory
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {
    private lateinit var callbackManager: CallbackManager
    private val EMAIL = "email"

    private val RC_SIGN_IN: Int = 0
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var isPassShowed = false

    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[LoginViewModel::class.java]
    }

    override fun getContentLayout(): Int {
        return R.layout.activity_login
    }

    override fun initView() {
        setLightIconStatusBar(true)
        binding.layout.setPadding(0, getStatusBarHeight(this@LoginActivity), 0, 0)

        binding.tvRegister.text = getTextHtml(R.string.str_register_next)
    }

    override fun initListener() {
        binding.tvRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnNext.setOnClickListener {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun observerLiveData() {
        viewModel.apply {

        }
    }

}