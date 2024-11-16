package com.ptit.signlanguage.ui.login

import android.content.Intent
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseActivity
import com.ptit.signlanguage.data.prefs.PreferencesHelper
import com.ptit.signlanguage.databinding.ActivityLoginBinding
import com.ptit.signlanguage.network.api.RetrofitBuilder.resetApiService
import com.ptit.signlanguage.network.model.response.User
import com.ptit.signlanguage.ui.main.MainActivity
import com.ptit.signlanguage.ui.register.RegisterActivity
import com.ptit.signlanguage.utils.Constants.KEY_PREF_DATA_LOGIN
import com.ptit.signlanguage.utils.Constants.KEY_TOKEN
import com.ptit.signlanguage.utils.GsonUtils
import com.ptit.signlanguage.view_model.ViewModelFactory


class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {
    private var isPassShowed = false
    private lateinit var prefsHelper: PreferencesHelper
    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory)[LoginViewModel::class.java]
    }

    override fun getContentLayout(): Int {
        return R.layout.activity_login
    }

    override fun initView() {
        setLightIconStatusBar(true)
        binding.layout.setPadding(0, getStatusBarHeight(this@LoginActivity), 0, 0)
        binding.tvRegister.text = getTextHtml(R.string.str_register_next)
        prefsHelper = PreferencesHelper(this@LoginActivity)
    }

    override fun initListener() {
        binding.tvRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
        binding.btnNext.setOnClickListener {
            if (!isDoubleClick()) {
                val email = binding.edtEmail.text.toString().trim()
                val pass = binding.edtPassword.text.toString().trim()
                viewModel.login(email, pass)
            }

        }

        binding.imvShowHidePass.setOnClickListener {
            if (isPassShowed) {
                isPassShowed = false
                binding.imvShowHidePass.setImageResource(R.drawable.ic_eye_closed)
                binding.edtPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.edtPassword.setSelection(binding.edtPassword.length())
            } else {
                isPassShowed = true
                binding.imvShowHidePass.setImageResource(R.drawable.ic_eye_open)
                binding.edtPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.edtPassword.setSelection(binding.edtPassword.length())
            }
        }
    }

    override fun observerLiveData() {
        viewModel.apply {
            loginResponse.observe(this@LoginActivity) {
                if (it?.body != null) {
                    val user = it.body
                    user.password = binding.edtPassword.text.trim().toString()
                    user.email = binding.edtEmail.text.trim().toString()
                    val dataLogin = GsonUtils.serialize(user, User::class.java)
                    prefsHelper.save(KEY_PREF_DATA_LOGIN, dataLogin)
                    prefsHelper.save(KEY_TOKEN, it.body.token?.accessToken)
                    resetApiService()
                    goToMain()
                } else {
                    Toast.makeText(this@LoginActivity, it?.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
            errorMessage.observe(this@LoginActivity) {
                Toast.makeText(this@LoginActivity, getString(it), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun goToMain() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}


