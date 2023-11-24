package com.ptit.signlanguage.base

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<VM : BaseViewModel, BINDING : ViewDataBinding> : Fragment() {

    lateinit var binding: BINDING
    lateinit var viewModel: VM
    var loadingDialog: LoadingDialog? = null
    protected lateinit var TAG: String

    private val double_press_interval: Long = 1000
    private var mLastClickTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, getContentLayout(), container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        TAG = this::class.java.simpleName

        initViewModel()
        observerDefaultLiveData()
        initView()
        initListener()
        observerLiveData()
    }

    abstract fun initViewModel()

    private fun observerDefaultLiveData() {
        loadingDialog = context?.let { LoadingDialog(it) }
        activity?.let { it ->
            viewModel.isLoading.observe(it) {
                if (it) {
                    loadingDialog?.show()
                } else {
                    loadingDialog?.hide()
                }
            }
        }
    }

    abstract fun getContentLayout(): Int

    abstract fun observerLiveData()

    abstract fun initView()

    abstract fun initListener()

    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun pxToDp(px: Int): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun isDoubleClick(): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < double_press_interval) {
            return true
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        return false
    }
}