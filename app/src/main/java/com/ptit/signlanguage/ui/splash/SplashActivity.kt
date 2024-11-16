package com.ptit.signlanguage.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseActivity
import com.ptit.signlanguage.data.prefs.PreferencesHelper
import com.ptit.signlanguage.databinding.ActivityLoginBinding
import com.ptit.signlanguage.network.api.RetrofitBuilder.resetApiService
import com.ptit.signlanguage.network.model.response.User
import com.ptit.signlanguage.ui.login.LoginActivity
import com.ptit.signlanguage.ui.login.LoginViewModel
import com.ptit.signlanguage.ui.main.MainActivity
import com.ptit.signlanguage.ui.welcome.WelcomeActivity
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.utils.GsonUtils
import com.ptit.signlanguage.utils.Language
import com.ptit.signlanguage.utils.Locale
import com.ptit.signlanguage.view_model.ViewModelFactory

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {
    private lateinit var prefsHelper: PreferencesHelper
    private var user : User? =  null
    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[LoginViewModel::class.java]
    }

    override fun getContentLayout(): Int {
        return R.layout.activty_splash
    }

    override fun initView() {
        setLightIconStatusBar(true)
        prefsHelper = PreferencesHelper(this@SplashActivity)
        autoLogin()
    }

    private fun autoLogin() {
        val dataLogin = prefsHelper.getString(Constants.KEY_PREF_DATA_LOGIN)
        user = GsonUtils.deserialize(dataLogin, User::class.java)

        if(user?.email.isNullOrEmpty() || user?.password.isNullOrEmpty()) {
            goToLogin()
        } else {
            viewModel.login(user?.email!!, user?.password!!)
        }
    }

    private fun changeLanguage() {
        val resources: Resources = this@SplashActivity.resources
        val configuration: Configuration = resources.configuration
        Log.d("tagneh", user?.language.toString())
        val locale = java.util.Locale(if (user?.language.equals(Constants.EN)) "en" else "vi")
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
    override fun initListener() {

    }

    override fun observerLiveData() {
        viewModel.apply {
            loginResponse.observe(this@SplashActivity) {
                if (it?.body != null) {
                    val pass = user?.password
                    user = it.body
                    user?.password = pass
                    val dataLogin = GsonUtils.serialize(user, User::class.java)
                    prefsHelper.save(Constants.KEY_PREF_DATA_LOGIN, dataLogin)
                    prefsHelper.save(Constants.KEY_TOKEN, it.body.token?.accessToken)
                    resetApiService()
                    changeLanguage()
                    goToMain()
                } else {
                    Toast.makeText(this@SplashActivity, it?.message.toString(), Toast.LENGTH_LONG).show()
                    goToLogin()
                }
            }
            errorMessage.observe(this@SplashActivity) {
                Toast.makeText(this@SplashActivity, getString(it), Toast.LENGTH_LONG).show()
                goToLogin()
            }
        }
    }

    private fun goToMain() {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToLogin() {
        val intent = Intent(this@SplashActivity, WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}