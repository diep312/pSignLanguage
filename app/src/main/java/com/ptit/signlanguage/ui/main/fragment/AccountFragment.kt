package com.ptit.signlanguage.ui.main.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import androidx.lifecycle.ViewModelProvider
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseFragment
import com.ptit.signlanguage.data.prefs.PreferencesHelper
import com.ptit.signlanguage.databinding.FragmentAccountBinding
import com.ptit.signlanguage.network.model.request.UpdateUserRequest
import com.ptit.signlanguage.network.model.response.User
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.utils.Constants.EMPTY_STRING
import com.ptit.signlanguage.utils.DateUtils
import com.ptit.signlanguage.utils.GsonUtils
import com.ptit.signlanguage.view_model.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class AccountFragment : BaseFragment<MainViewModel, FragmentAccountBinding>() {
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var prefsHelper: PreferencesHelper
    lateinit var calendar: Calendar
    var user: User? = null

    private fun initDatePickerDialog() {
        val dateInit = DateUtils.convertStringToDate(
            user?.dateOfBirth ?: EMPTY_STRING,
            Constants.DATE_SIMPLE_FORMAT
        )
        if (dateInit != null) {
            calendar.time = dateInit
        }
        datePickerDialog = DatePickerDialog(
            binding.root.context, R.style.customDatePicker,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat(Constants.DATE_SIMPLE_FORMAT, Locale.getDefault())
                dateFormat.calendar = calendar
                val date: String = dateFormat.format(calendar.time)
//                val date : Date = Date(year, month, dayOfMonth)
                binding.edtBirthday.text = date
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
    }

    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
    }

    override fun getContentLayout(): Int {
        return R.layout.fragment_account
    }

    override fun observerLiveData() {
        viewModel.apply {
            binding
        }
    }

    override fun initView() {
        prefsHelper = PreferencesHelper(binding.root.context)
        calendar = Calendar.getInstance()
//        initDatePickerDialog()

        val userJson = prefsHelper.getString(Constants.KEY_PREF_DATA_LOGIN)
        user = GsonUtils.deserialize(userJson, User::class.java)
        user?.apply {
            binding.edtName.setText(this.name ?: EMPTY_STRING)
            binding.edtBirthday.text = this.dateOfBirth ?: EMPTY_STRING
            binding.edtAddress.setText(this.address ?: EMPTY_STRING)
            binding.edtEmail.setText(this.email ?: EMPTY_STRING)
            binding.edtPhone.setText(this.phoneNumber ?: EMPTY_STRING)
            binding.edtProfileNum.setText(this.profileNumber ?: EMPTY_STRING)
            binding.edtSupport.setText(this.supportedBy ?: EMPTY_STRING)
            binding.edtRegisterNum.setText(this.registerType ?: EMPTY_STRING)
        }

    }

    override fun initListener() {
        binding.imvLogout.setOnClickListener {
            showDialogConfirmLogout()
        }

        binding.edtBirthday.setOnClickListener {
            initDatePickerDialog()
            datePickerDialog.show()
        }

        binding.btnSave.setOnClickListener {
            if(!isDoubleClick()) {
                var request = UpdateUserRequest()
                request.address = binding.edtAddress.text.toString()
                request.dateOfBirth = binding.edtBirthday.text.toString()
                request.email = binding.edtEmail.text.toString()
//                request.language = binding.edtEmail.text.toString()
                request.name = binding.edtName.text.toString()
                request.phoneNumber = binding.edtPhone.text.toString()
                request.registerType = binding.edtRegisterNum.text.toString()
                request.supportedBy = binding.edtSupport.text.toString()
                request.supportedBy = binding.edtProfileNum.text.toString()

                viewModel.updateUser(request)
            }
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