package com.ptit.signlanguage.ui.register

import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
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

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val face = ResourcesCompat.getFont(this, R.font.montserrat_semi_bold)

        binding.collapseToolbar.setCollapsedTitleTypeface(face)
        binding.collapseToolbar.setExpandedTitleTypeface(face)
        binding.collapseToolbar.title = getString(R.string.str_register)
        binding.collapseToolbar.setCollapsedTitleTextColor(
            ContextCompat.getColor(
                this,
                R.color.black
            )
        )
        binding.collapseToolbar.setExpandedTitleColor(
            ContextCompat.getColor(
                this,
                R.color.black
            )
        )
    }

    override fun initListener() {
//        binding.imvBack.setOnClickListener {
//            finish()
//        }
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

//    fun validateUser(email : String, pass : String) : Boolean {
//        if(email.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            Toast.makeText(this@LoginActivity, "Email không hợp lệ", Toast.LENGTH_LONG).show()
//            return false
//        }
//        if(pass.isEmpty() || pass.length < 6) {
//
//        }
//    }

}