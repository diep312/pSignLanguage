package com.ptit.signlanguage.ui.main.fragment

import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseFragment
import com.ptit.signlanguage.base.onTextChange
import com.ptit.signlanguage.data.prefs.PreferencesHelper
import com.ptit.signlanguage.databinding.FragmentTextToVideoBinding
import com.ptit.signlanguage.network.model.response.Label
import com.ptit.signlanguage.network.model.response.User
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.ui.main.adapter.LabelAdapter
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.utils.Constants.EN
import com.ptit.signlanguage.utils.Constants.VI
import com.ptit.signlanguage.utils.GsonUtils
import com.ptit.signlanguage.view_model.ViewModelFactory
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import okhttp3.internal.notify

class ListLabelFragment : BaseFragment<MainViewModel, FragmentTextToVideoBinding>() {
    lateinit var adapter: LabelAdapter
    var user : User? = null
    private lateinit var prefsHelper: PreferencesHelper
    private val scope = CoroutineScope(Dispatchers.IO) + CoroutineExceptionHandler { _, exception ->
        {
            Log.d("Error", exception.message.toString())
        }
    }
    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory)[MainViewModel::class.java]
    }

    override fun getContentLayout(): Int {
        return R.layout.fragment_text_to_video
    }

    override fun observerLiveData() {
        viewModel.apply {
            listLabelRes.observe(this@ListLabelFragment) {
                if (it?.body != null) {
                    adapter.replace(it.body.toMutableList())
                }
            }
            errorMessage.observe(this@ListLabelFragment) {
                Toast.makeText(binding.root.context, getString(it), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun initView() {
        prefsHelper = PreferencesHelper(binding.root.context)
        val userJson = prefsHelper.getString(Constants.KEY_PREF_DATA_LOGIN)
        user = GsonUtils.deserialize(userJson, User::class.java)

        adapter = if(user?.language.equals(EN)) {
            LabelAdapter(mutableListOf(), EN,requireContext())
        } else {
            LabelAdapter(mutableListOf(), VI, requireContext())
        }
        adapter.getVideoCallback = { label ->
            callbackFlow {
                trySend(viewModel.getVideoFlow(label))
                awaitClose()
            }
        }
        binding.rvLabel.adapter = adapter
        viewModel.getListLabel()
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    override fun initListener() {
        binding.edtSearch.onTextChange()
            .debounce(300L)
            .onEach {
                Log.d("Test", it)
                if(it.isEmpty()){
                    adapter.listLabel.forEach{
                        label: Label? -> label?.isShow = true
                    }
                }
                else{
                    for (label in adapter.listLabel) {
                        if((user?.language.equals(EN) && label?.labelEn?.startsWith( it, true) == true) ||
                            (user?.language.equals(VI) && label?.labelVn?.startsWith(it, true) == true )) {
                            label.isShow = true
                        } else {
                            label?.isShow = false
                        }
                    }
                    loadingDialog?.show()
                    adapter.listLabel.sortBy { label ->
                        if(user?.language.equals(EN)) label?.labelEn
                        else label?.labelVn
                    }
                    loadingDialog?.hide()
                }
                withContext(Dispatchers.Main){adapter.notifyDataSetChanged()}
            }.launchIn(scope = lifecycleScope)
    }



}