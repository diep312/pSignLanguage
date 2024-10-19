package com.ptit.signlanguage.ui.main.dialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.ptit.signlanguage.R
import com.ptit.signlanguage.databinding.DialogUserInformationEditBinding
import com.ptit.signlanguage.network.model.request.UpdateUserRequest
import com.ptit.signlanguage.network.model.response.User
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.utils.Constants.EMPTY_STRING
import com.ptit.signlanguage.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class UserInformationEditDialog : DialogFragment() {
    private var userJson: User? = null
    private lateinit var binding : DialogUserInformationEditBinding
    private var onSaveInformationClick: (a: UpdateUserRequest) -> Unit = {}
    private var calendar: Calendar? = null
    private lateinit var datePickerDialog: DatePickerDialog
    companion object{
        const val TAG = "UserInformationEditDialog"
        fun newInstance(userJson: User, onSaveInformationClick: (a: UpdateUserRequest) -> Unit): UserInformationEditDialog {
            return UserInformationEditDialog().apply {
                this.userJson = userJson
                this.onSaveInformationClick = onSaveInformationClick
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        return Dialog(requireContext(), R.style.CustomUserEditDialog)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calendar = Calendar.getInstance()
    }

    override fun onResume() {
        super.onResume()
        val window = dialog!!.window ?: return
        val params = window.attributes
        window.apply {
            decorView.setPadding(24,8,24,8)
        }
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        window.attributes = params
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_user_information_edit, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        binding.btnSave.setOnClickListener {
            onSaveInformationClick(getRequestData())
            dismiss()
        }
        binding.edtBirthday.setOnClickListener{
            initDatePickerDialog()
        }
    }
    private fun initView(){
        userJson?.let {
            binding.edtName.setText(it.name ?: EMPTY_STRING)
            binding.edtBirthday.text = it.dateOfBirth ?: EMPTY_STRING
            binding.edtAddress.setText(it.address ?: EMPTY_STRING)
            binding.edtEmail.setText(it.email ?: EMPTY_STRING)
            binding.edtPhone.setText(it.phoneNumber ?: EMPTY_STRING)
        }
    }
    private fun getRequestData(): UpdateUserRequest {
        return UpdateUserRequest().apply {
            address = binding.edtAddress.text.toString()
            dateOfBirth = binding.edtBirthday.text.toString()
            email = binding.edtEmail.text.toString()
            name = binding.edtName.text.toString()
            phoneNumber = binding.edtPhone.text.toString()
        }
    }
    private fun initDatePickerDialog() {
        val dateInit = DateUtils.convertStringToDate(
            userJson?.dateOfBirth ?: EMPTY_STRING,
            Constants.DATE_SIMPLE_FORMAT
        )
        if (dateInit != null) {
            calendar?.time = dateInit
        }
        datePickerDialog = DatePickerDialog(
            binding.root.context, R.style.customDatePicker,
            { _, year, month, dayOfMonth ->
                calendar!!.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat(Constants.DATE_SIMPLE_FORMAT, Locale.getDefault())
                dateFormat.calendar = calendar!!
                val date: String = dateFormat.format(calendar!!.time)
//                val date : Date = Date(year, month, dayOfMonth)
                binding.edtBirthday.text = date
            },
            calendar!!.get(Calendar.YEAR),
            calendar!!.get(Calendar.MONTH),
            calendar!!.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
    }
}