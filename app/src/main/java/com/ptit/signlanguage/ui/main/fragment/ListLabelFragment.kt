package com.ptit.signlanguage.ui.main.fragment

import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseFragment
import com.ptit.signlanguage.databinding.FragmentTextToVideoBinding
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.ui.main.adapter.LabelAdapter
import com.ptit.signlanguage.view_model.ViewModelFactory

class ListLabelFragment : BaseFragment<MainViewModel, FragmentTextToVideoBinding>() {
    var adapter: LabelAdapter = LabelAdapter(mutableListOf())

    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
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
        binding.rvLabel.adapter = adapter
//        binding.rvLabel.addItemDecoration(LinearItemDecoration(dpToPx(12), VERTICAL))
        viewModel.getListLabel()
    }

    override fun initListener() {
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Handler().postDelayed({
                    val item = binding.edtSearch.text.toString().trim()
                    for (label in adapter.listLabel) {
                        if(label?.labelEn?.contains(item, true) == true ||
                            label?.labelVn?.contains(item, true) == true ) {
                            label.isShow = true
                        } else {
                            label?.isShow = false
                        }
                    }
                    adapter.notifyDataSetChanged()
                }, 100)

            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }



}