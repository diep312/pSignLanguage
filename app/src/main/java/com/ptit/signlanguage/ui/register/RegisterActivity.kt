package com.ptit.signlanguage.ui.register

import android.content.Intent
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseActivity
import com.ptit.signlanguage.data.prefs.PreferencesHelper
import com.ptit.signlanguage.databinding.ActivityRegisterBinding
import com.ptit.signlanguage.network.api.RetrofitBuilder.resetApiService
import com.ptit.signlanguage.network.model.response.User
import com.ptit.signlanguage.ui.login.LoginActivity
import com.ptit.signlanguage.ui.login.LoginViewModel
import com.ptit.signlanguage.ui.main.MainActivity
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.utils.GsonUtils
import com.ptit.signlanguage.view_model.ViewModelFactory


class RegisterActivity : BaseActivity<LoginViewModel, ActivityRegisterBinding>() {
    private lateinit var prefsHelper: PreferencesHelper
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

        prefsHelper = PreferencesHelper(this@RegisterActivity)
    }

    override fun initListener() {
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

        binding.btnNext.setOnClickListener {
            if (!isDoubleClick()) {
                val name = binding.edtUsername.text.toString().trim()
                val email = binding.edtEmail.text.toString().trim()
                val pass = binding.edtPassword.text.toString().trim()
                val passAgain = binding.edtPassAgain.text.toString().trim()
                if (validateUser(name, email, pass, passAgain)) {
                    viewModel.register(name, email, pass)
                }
            }
        }
    }

    override fun observerLiveData() {
        viewModel.apply {
            registerResponse.observe(this@RegisterActivity) {
                if (it?.body == null) {
                    Toast.makeText(this@RegisterActivity, it?.message.toString(), Toast.LENGTH_LONG)
                        .show()
                } else {
                    val name = binding.edtUsername.text.toString().trim()
                    val email = binding.edtEmail.text.toString().trim()
                    val pass = binding.edtPassword.text.toString().trim()
                    val user = User(email = email, password = pass, name = name, token = it.body)
                    val dataLogin = GsonUtils.serialize(user, User::class.java)
                    prefsHelper.save(Constants.KEY_PREF_DATA_LOGIN, dataLogin)
                    prefsHelper.save(Constants.KEY_TOKEN, it.body.accessToken)
                    resetApiService()
                    goToLogin()
                }
            }
            errorMessage.observe(this@RegisterActivity) {
                Toast.makeText(this@RegisterActivity, getString(it), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validateUser(
        name: String?,
        email: String?,
        pass: String?,
        passAgain: String?
    ): Boolean {
        if (name.isNullOrEmpty() || name.length < 6) {
            Toast.makeText(
                this@RegisterActivity,
                "Tên hiển thị phải nhiều hơn 6 kí tự!",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        if (email.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this@RegisterActivity, "Email không hợp lệ!", Toast.LENGTH_LONG).show()
            return false
        }
        if (pass.isNullOrEmpty() || pass.length < 6 || passAgain.isNullOrEmpty() || passAgain.length < 6) {
            Toast.makeText(
                this@RegisterActivity,
                "Mật khẩu phải nhiều hơn 6 kí tự!",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        if (pass != passAgain) {
            Toast.makeText(this@RegisterActivity, "Mật khẩu không khớp!", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun goToMain() {
        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToLogin() {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

}