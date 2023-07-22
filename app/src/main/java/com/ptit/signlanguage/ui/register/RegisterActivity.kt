package com.ptit.signlanguage.ui.register

import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseActivity
import com.ptit.signlanguage.databinding.ActivityRegisterBinding
import com.ptit.signlanguage.ui.login.LoginViewModel
import com.ptit.signlanguage.view_model.ViewModelFactory


class RegisterActivity : BaseActivity<LoginViewModel, ActivityRegisterBinding>() {
    private var isPassShowed = false
    private var isPassRepeatShowed = false

    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[LoginViewModel::class.java]
    }

    override fun getContentLayout(): Int {
        return R.layout.activity_register
    }

    override fun initView() {
        setLightIconStatusBar(true)
        binding.layout.setPadding(0, getStatusBarHeight(this@RegisterActivity), 0, 0)

        binding.tvLogin.text = getTextHtml(R.string.str_login_next)
    }

    override fun initListener() {
        binding.imvBack.setOnClickListener {
            finish()
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
        binding.imvShowHidePassAgain.setOnClickListener {
            if (isPassRepeatShowed) {
                isPassRepeatShowed = false
                binding.imvShowHidePassAgain.setImageResource(R.drawable.ic_eye_closed)
                binding.edtPassAgain.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.edtPassAgain.setSelection(binding.edtPassAgain.length())
            } else {
                isPassRepeatShowed = true
                binding.imvShowHidePassAgain.setImageResource(R.drawable.ic_eye_open)
                binding.edtPassAgain.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.edtPassAgain.setSelection(binding.edtPassAgain.length())
            }
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }

    override fun observerLiveData() {
    }

}