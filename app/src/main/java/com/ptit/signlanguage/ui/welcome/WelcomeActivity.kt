package com.ptit.signlanguage.ui.welcome

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseActivity
import com.ptit.signlanguage.databinding.ActivityWelcomeScreenBinding
import com.ptit.signlanguage.ui.login.LoginActivity

class WelcomeActivity : AppCompatActivity(){
    private val binding: ActivityWelcomeScreenBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_welcome_screen)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setColorForStatusBar(R.color.primary)
        setContentView(binding.root)
        onAction()
    }
    private fun onAction(){
        binding.button.setOnClickListener{
            val intent = Intent(this,LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }
    fun setColorForStatusBar(color: Int) {
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.statusBarColor = ContextCompat.getColor(this, color)
    }
}