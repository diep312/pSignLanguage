package com.ptit.signlanguage.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.text.Html
import android.text.Spanned
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseActivity<VM : BaseViewModel, BINDING : ViewDataBinding> : AppCompatActivity() {
    protected lateinit var viewModel: VM
    protected lateinit var binding: BINDING
    protected lateinit var TAG: String
    private val double_press_interval: Long = 1000
    private var mLastClickTime: Long = 0
    lateinit var loadingDialog: LoadingDialog

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        window.addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON)
        binding = DataBindingUtil.setContentView(this, getContentLayout())
        TAG = this::class.java.simpleName
        setContentView(binding.root)
        initViewModel()
        initLoadingDialog()
        observerDefaultLiveData()
        initView()
        initListener()
        observerLiveData()
    }

    private fun initLoadingDialog() {
        loadingDialog = LoadingDialog(this)
    }

    abstract fun initViewModel()

    abstract fun getContentLayout(): Int

    private fun observerDefaultLiveData() {
        viewModel.isLoading.observe(this) {
            if (it) {
                CoroutineScope(Dispatchers.Main).launch{
                    loadingDialog.show()
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    loadingDialog.hide()
                }
            }
        }
    }

    abstract fun initView()

    abstract fun initListener()

    abstract fun observerLiveData()

    open fun isDoubleClick(): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < double_press_interval) {
            return true
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        return false
    }

    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun transparentStatusBar() {
        window.statusBarColor = 0x00000000
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    fun setColorForStatusBar(color: Int) {
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.statusBarColor = ContextCompat.getColor(this, color)
    }

    fun setLightIconStatusBar(isLight: Boolean) {
        window.statusBarColor = 0x00000000
        var flags: Int = window.decorView.systemUiVisibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags =
                if (isLight) {
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    0
                }
        }
        window.decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    fun getTextHtml(stringResID: Int): Spanned =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(getString(stringResID), Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(getString(stringResID))
        }

    fun pxToDp(px: Int): Int = (px / Resources.getSystem().displayMetrics.density).toInt()

    fun dpToPx(dp: Int): Int = (dp * Resources.getSystem().displayMetrics.density).toInt()
}
