package com.nguyencuong.mybasekotlin.ui.register

import android.R.attr.phoneNumber
import android.content.Intent
import android.os.Build
import android.text.Html
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.nguyencuong.mybasekotlin.R
import com.nguyencuong.mybasekotlin.base.BaseActivity
import com.nguyencuong.mybasekotlin.databinding.ActivityRegisterBinding
import com.nguyencuong.mybasekotlin.ui.login.LoginViewModel
import com.nguyencuong.mybasekotlin.ui.main.MainActivity
import com.nguyencuong.mybasekotlin.ui.verify.VerifyActivity
import com.nguyencuong.mybasekotlin.utils.Constants.FIRST_PHONE_VN
import com.nguyencuong.mybasekotlin.utils.Constants.KEY_PHONE_NUMBER
import com.nguyencuong.mybasekotlin.utils.Constants.KEY_RESEND_TOKEN
import com.nguyencuong.mybasekotlin.utils.Constants.KEY_VERIFICATION_ID
import com.nguyencuong.mybasekotlin.view_model.ViewModelFactory
import java.util.concurrent.TimeUnit


class RegisterActivity : BaseActivity<LoginViewModel, ActivityRegisterBinding>() {
    private var isPassShowed = false
    private var isPassRepeatShowed = false
    lateinit var mAuth : FirebaseAuth
//    private val TAG: String = "RegisterActivity"

    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[LoginViewModel::class.java]
    }

    override fun getContentLayout(): Int {
        return R.layout.activity_register
    }

    override fun initView() {
        setLightIconStatusBar(true)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            binding.tvLogin.text = Html.fromHtml(getString(R.string.str_require_login))
        } else {
            binding.tvLogin.text = Html.fromHtml(getString(R.string.str_require_login), Html.FROM_HTML_MODE_LEGACY)
        }

        mAuth = FirebaseAuth.getInstance()
//        mAuth.setLanguageCode("vi")
//        mAuth.useAppLanguage()

    }

    override fun initListener() {
        binding.tvLogin.setOnClickListener {
            finish()
        }
        binding.imvBack.setOnClickListener {
            finish()
        }
        binding.imvShowHidePass.setOnClickListener {
            if(isPassShowed) {
                isPassShowed = false
                binding.imvShowHidePass.setImageResource(R.drawable.ic_eye_closed)
                binding.edtPass.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.edtPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.edtPass.setSelection(binding.edtPass.length())
            } else {
                isPassShowed = true
                binding.imvShowHidePass.setImageResource(R.drawable.ic_eye_open)
                binding.edtPass.transformationMethod = HideReturnsTransformationMethod.getInstance();
                binding.edtPass.inputType = InputType.TYPE_CLASS_TEXT;
                binding.edtPass.setSelection(binding.edtPass.length())
            }
        }
        binding.imvShowHidePassRepeat.setOnClickListener {
            if(isPassRepeatShowed) {
                isPassRepeatShowed = false
                binding.imvShowHidePassRepeat.setImageResource(R.drawable.ic_eye_closed)
                binding.edtPassRepeat.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.edtPassRepeat.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.edtPassRepeat.setSelection(binding.edtPassRepeat.length())
            } else {
                isPassRepeatShowed = true
                binding.imvShowHidePassRepeat.setImageResource(R.drawable.ic_eye_open)
                binding.edtPassRepeat.transformationMethod = HideReturnsTransformationMethod.getInstance();
                binding.edtPassRepeat.inputType = InputType.TYPE_CLASS_TEXT;
                binding.edtPassRepeat.setSelection(binding.edtPassRepeat.length())
            }
        }

        binding.btnRegister.setOnClickListener {
            viewModel.showLoading()

            val options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(binding.edtPhone.text.toString().trim().replaceFirst("0", FIRST_PHONE_VN))       // Phone number to verify
                .setTimeout(90L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)

        }

    }

    override fun observerLiveData() {
    }

    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

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
                Toast.makeText(this@RegisterActivity, getString(R.string.str_verify_fail), Toast.LENGTH_LONG).show()
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Toast.makeText(this@RegisterActivity, getString(R.string.str_too_many_request), Toast.LENGTH_LONG).show()
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
//            storedVerificationId = verificationId
//            resendToken = token

            val intent = Intent(this@RegisterActivity, VerifyActivity::class.java)
            intent.putExtra(KEY_PHONE_NUMBER,
                binding.edtPhone.text.toString().trim().replaceFirst("0", FIRST_PHONE_VN))
            intent.putExtra(KEY_VERIFICATION_ID, verificationId)
            intent.putExtra(KEY_RESEND_TOKEN, token)
            startActivity(intent)
        }
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
                            Toast.makeText(this@RegisterActivity, getString(R.string.str_otp_fail), Toast.LENGTH_LONG).show()
                    }
                    // Update UI
                }
            }
    }

    private fun goToMainActivity(user: FirebaseUser?) {
        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
        intent.putExtra(KEY_PHONE_NUMBER, user?.phoneNumber)
        startActivity(intent)
    }

}