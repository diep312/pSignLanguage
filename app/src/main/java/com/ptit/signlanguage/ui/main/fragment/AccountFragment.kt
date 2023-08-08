package com.ptit.signlanguage.ui.main.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import androidx.lifecycle.ViewModelProvider
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseFragment
import com.ptit.signlanguage.databinding.FragmentAccountBinding
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.view_model.ViewModelFactory
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class AccountFragment : BaseFragment<MainViewModel, FragmentAccountBinding>() {
    private lateinit var datePickerDialog: DatePickerDialog
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
        calendar = Calendar.getInstance()
        initDatePickerDialog()
    }

    override fun initListener() {
        binding.imvLogout.setOnClickListener { activity?.finish() }

        binding.edtBirthday.setOnClickListener {
            datePickerDialog.show()
        }
    }
}