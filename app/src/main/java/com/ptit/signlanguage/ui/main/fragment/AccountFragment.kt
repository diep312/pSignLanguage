package com.ptit.signlanguage.ui.main.fragment

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseFragment
import com.ptit.signlanguage.data.prefs.PreferencesHelper
import com.ptit.signlanguage.databinding.FragmentAccountBinding
import com.ptit.signlanguage.network.model.request.UpdateUserRequest
import com.ptit.signlanguage.network.model.response.User
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.ui.main.dialog.UserInformationEditDialog
import com.ptit.signlanguage.ui.splash.SplashActivity
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.utils.Constants.EMPTY_STRING
import com.ptit.signlanguage.utils.GsonUtils
import com.ptit.signlanguage.view_model.ViewModelFactory


class AccountFragment : BaseFragment<MainViewModel, FragmentAccountBinding>() {
    private lateinit var prefsHelper: PreferencesHelper
    private lateinit var dialogUserEdit: UserInformationEditDialog
    private lateinit var request: UpdateUserRequest
    var user: User? = null



    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
        }

    override fun getContentLayout(): Int {
        return R.layout.fragment_account
    }

    override fun observerLiveData() {
        viewModel.apply {
            updateUserRes.observe(this@AccountFragment) {
                val intent = Intent(this@AccountFragment.context, SplashActivity::class.java)
                startActivity(intent)
                this@AccountFragment.activity?.finishAffinity()
            }
            errorMessage.observe(this@AccountFragment) {
                Toast.makeText(this@AccountFragment.context, getString(it), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun initView() {
        prefsHelper = PreferencesHelper(binding.root.context)
        val userJson = prefsHelper.getString(Constants.KEY_PREF_DATA_LOGIN)
        user = GsonUtils.deserialize(userJson, User::class.java)
        user?.apply {
            binding.tvShowName.text = this.name?: EMPTY_STRING
        }
    }

    override fun initListener() {
        binding.imvLogout.setOnClickListener {
            showDialogConfirmLogout()
        }

        binding.btnSave.setOnClickListener {
            if(!isDoubleClick()) {
                viewModel.updateUser(request)
            }
        }
        binding.editAccountInfo.setOnClickListener{
            val ft: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()

            dialogUserEdit = UserInformationEditDialog.newInstance(user!!){ request ->
                viewModel.updateUser(request)
            }
            dialogUserEdit.show(ft, "dialog")
        }
        dataPrepareSpinner()
        binding.languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                request = UpdateUserRequest()
                request.address = user!!.address
                request.dateOfBirth = user!!.dateOfBirth
                request.email = user!!.email
                if (id.toInt() == 1) {
                    request.language = "EN"
                } else {
                    request.language = "VI"
                }
                request.name = user!!.name
                request.phoneNumber = user!!.phoneNumber
                Log.d("tagneh123", request.language.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }
    private fun dataPrepareSpinner() {
        val language = arrayOf("Tiếng Việt", "English")
        val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, language)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.languageSpinner.adapter = adapter
        if(user!!.language.equals("EN")) {
            binding.languageSpinner.setSelection(1)
        } else {
            binding.languageSpinner.setSelection(0)
        }
    }
    private fun showDialogConfirmLogout() {
        val builder = AlertDialog.Builder(this@AccountFragment.context)
        builder.setMessage(getString(R.string.str_confirm_logout))
        builder.setCancelable(true)

        builder.setPositiveButton(
            getString(R.string.str_ok)
        ) { _, _ ->
            prefsHelper.remove(Constants.KEY_PREF_DATA_LOGIN)
            prefsHelper.remove(Constants.KEY_TOKEN)
            activity?.finish()
        }

        builder.setNegativeButton(
            getString(R.string.str_cancel)

        ) { dialog, _ ->
            dialog.cancel()

        }
        builder.create().show()
    }
}