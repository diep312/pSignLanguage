package com.ptit.signlanguage.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<VM : BaseViewModel, BINDING : ViewDataBinding> : Fragment() {

    private lateinit var binding: BINDING
    private lateinit var viewModel: VM
    var loadingDialog: LoadingDialog? = null

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
}