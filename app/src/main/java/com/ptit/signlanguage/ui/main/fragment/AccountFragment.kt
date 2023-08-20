package com.ptit.signlanguage.ui.main.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import androidx.lifecycle.ViewModelProvider
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseFragment
import com.ptit.signlanguage.data.prefs.PreferencesHelper
import com.ptit.signlanguage.databinding.FragmentAccountBinding
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.view_model.ViewModelFactory
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class AccountFragment : BaseFragment<MainViewModel, FragmentAccountBinding>() {
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var prefsHelper: PreferencesHelper
    lateinit var calendar: Calendar

    private fun initDatePickerDialog() {
        datePickerDialog = DatePickerDialog(
            binding.root.context, R.style.customDatePicker,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat(Constants.DATE_SIMPLE_FORMAT, Locale.getDefault())
//                dateFormat.calendar = calendar
                val date: String = dateFormat.format(calendar.time)
                binding.edtBirthday.text = date
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
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

        }
    }

    override fun initView() {
        prefsHelper = PreferencesHelper(binding.root.context)
        calendar = Calendar.getInstance()
        initDatePickerDialog()
    }

    override fun initListener() {
        binding.imvLogout.setOnClickListener {
            showDialogConfirmLogout()
        }

        binding.edtBirthday.setOnClickListener {
            datePickerDialog.show()
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