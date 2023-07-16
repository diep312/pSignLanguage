package com.nguyencuong.mybasekotlin.ui.verify

import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.nguyencuong.mybasekotlin.R
import com.nguyencuong.mybasekotlin.base.BaseActivity
import com.nguyencuong.mybasekotlin.databinding.ActivityVerifyBinding
import com.nguyencuong.mybasekotlin.ui.login.LoginViewModel
import com.nguyencuong.mybasekotlin.ui.main.MainActivity
import com.nguyencuong.mybasekotlin.ui.register.RegisterActivity
import com.nguyencuong.mybasekotlin.utils.Constants
import com.nguyencuong.mybasekotlin.utils.Constants.KEY_PHONE_NUMBER
import com.nguyencuong.mybasekotlin.utils.Constants.KEY_VERIFICATION_ID
import com.nguyencuong.mybasekotlin.view_model.ViewModelFactory
import java.util.concurrent.TimeUnit

class VerifyActivity : BaseActivity<LoginViewModel, ActivityVerifyBinding>() {
    val listTvOtp = mutableListOf<TextView>()
    lateinit var timer: CountDownTimer

    lateinit var mPhoneNumber : String
    lateinit var mVerificationId: String
    lateinit var mToken: PhoneAuthProvider.ForceResendingToken
    lateinit var mAuth : FirebaseAuth
//    private val TAG: String = "VerifyActivity"

    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[LoginViewModel::class.java]
    }

    override fun getContentLayout(): Int {
        return R.layout.activity_verify
    }

    override fun initView() {
        setLightIconStatusBar(true)

        listTvOtp += binding.layoutOtp.tv1
        listTvOtp.add(binding.layoutOtp.tv2)
        listTvOtp.add(binding.layoutOtp.tv3)
        listTvOtp.add(binding.layoutOtp.tv4)
        listTvOtp.add(binding.layoutOtp.tv5)
        listTvOtp.add(binding.layoutOtp.tv6)

        mAuth = FirebaseAuth.getInstance()
//        mAuth.setLanguageCode("vi")
//        mAuth.useAppLanguage()
        mPhoneNumber = intent.getStringExtra(KEY_PHONE_NUMBER).toString()
        mVerificationId = intent.getStringExtra(KEY_VERIFICATION_ID).toString()
        mToken = intent.extras!!.getParcelable<PhoneAuthProvider.ForceResendingToken>(Constants.KEY_RESEND_TOKEN)!!

        timer = object : CountDownTimer(90000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvTimeCountDown.text = "Còn lại: ${millisUntilFinished / 1000}s"
            }

            override fun onFinish() {
                binding.tvTimeCountDown.isClickable = true
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    binding.tvTimeCountDown.text = Html.fromHtml(getString(R.string.str_require_send_otp))
                } else {
                    binding.tvTimeCountDown.text = Html.fromHtml(getString(R.string.str_require_send_otp), Html.FROM_HTML_MODE_LEGACY)
                }
            }
        }
        timer.start()

    }

    override fun initListener() {
        binding.imvBack.setOnClickListener {
            finish()
        }
        binding.layoutOtp.edtOtp.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                for (i in 0 until listTvOtp.size) {
                    if (i < s!!.length) {
                        listTvOtp[i].text = s[i].toString()
                    } else {
                        listTvOtp[i].text = ""
                    }
                }

                if (s!!.length == 6) {
//                    confirmOTPFirebase(s.toString())
                }
            }

        })

        binding.tvTimeCountDown.setOnClickListener {
            viewModel.showLoading()

            binding.tvTimeCountDown.isClickable = false
            val options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(mPhoneNumber)       // Phone number to verify
                .setTimeout(90L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this@VerifyActivity)                 // Activity (for callback binding)
                .setForceResendingToken(mToken)
                .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)

            timer.start()
        }
        binding.btnVerify.setOnClickListener {
            viewModel.showLoading()
            val credential = PhoneAuthProvider.getCredential(mVerificationId!!, binding.layoutOtp.edtOtp.text.toString().trim())
            signInWithPhoneAuthCredential(credential)
        }
    }

    override fun observerLiveData() {

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                viewModel.hideLoading()
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = task.result?.user
                    goToMainActivity(user)
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(this@VerifyActivity, getString(R.string.str_otp_fail), Toast.LENGTH_LONG).show()

                    }
                    // Update UI
                }
            }
    }

    private fun goToMainActivity(user: FirebaseUser?) {
        val intent = Intent(this@VerifyActivity, MainActivity::class.java)
        intent.putExtra(KEY_PHONE_NUMBER, user?.phoneNumber)
        startActivity(intent)
        finish()
    }


    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(TAG, "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            viewModel.hideLoading()
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Toast.makeText(this@VerifyActivity, getString(R.string.str_verify_fail), Toast.LENGTH_LONG).show()
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Toast.makeText(this@VerifyActivity, getString(R.string.str_too_many_request), Toast.LENGTH_LONG).show()
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            viewModel.hideLoading()
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:$verificationId")
            // Save verification ID and resending token so we can use them later
            mVerificationId = verificationId
            mToken = token
        }
    }
}