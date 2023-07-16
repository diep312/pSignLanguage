package com.nguyencuong.mybasekotlin.ui.login

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.text.Html
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.nguyencuong.mybasekotlin.R
import com.nguyencuong.mybasekotlin.base.BaseActivity
import com.nguyencuong.mybasekotlin.databinding.ActivityLoginBinding
import com.nguyencuong.mybasekotlin.ui.main.MainActivity
import com.nguyencuong.mybasekotlin.ui.register.RegisterActivity
import com.nguyencuong.mybasekotlin.utils.Constants.KEY_PHONE_NUMBER
import com.nguyencuong.mybasekotlin.view_model.ViewModelFactory
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {
    private lateinit var callbackManager: CallbackManager
    private val EMAIL = "email"

    private val RC_SIGN_IN: Int = 0
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var isPassShowed = false

    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[LoginViewModel::class.java]
    }

    override fun getContentLayout(): Int {
        return R.layout.activity_login
    }

    override fun initView() {
        setLightIconStatusBar(true)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            binding.tvRegister.text = Html.fromHtml(getString(R.string.str_require_register))
        } else {
            binding.tvRegister.text =
                Html.fromHtml(getString(R.string.str_require_register), Html.FROM_HTML_MODE_LEGACY)
        }

        initLoginGoogle()
        initLoginFacebook()

    }

    private fun initLoginFacebook() {
        // Facebook
        printHashKey(this)
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    // App code
                    // App code
                    val profile = Profile.getCurrentProfile()

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    Log.d(TAG, profile.id)
                    intent.putExtra(KEY_PHONE_NUMBER, profile.name)
                    startActivity(intent)
                }

                override fun onCancel() {
                    // App code
                }

                override fun onError(exception: FacebookException) {
                    // App code
                }
            })

    }

    private fun initLoginGoogle() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    override fun initListener() {
        binding.tvRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.imvShowHidePass.setOnClickListener {
            if (isPassShowed) {
                isPassShowed = false
                binding.imvShowHidePass.setImageResource(R.drawable.ic_eye_closed)
                binding.edtPass.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.edtPass.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.edtPass.setSelection(binding.edtPass.length())
            } else {
                isPassShowed = true
                binding.imvShowHidePass.setImageResource(R.drawable.ic_eye_open)
                binding.edtPass.transformationMethod =
                    HideReturnsTransformationMethod.getInstance();
                binding.edtPass.inputType = InputType.TYPE_CLASS_TEXT;
                binding.edtPass.setSelection(binding.edtPass.length())
            }
        }

        binding.imvGoogle.setOnClickListener {
            signIn()
        }

        binding.imvFacebook.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile"));
        }
    }

    override fun observerLiveData() {
        viewModel.apply {
            viewModel.mtoken.observe(this@LoginActivity) {
                viewModel.hideLoading()
                Log.d(TAG, it.toString())
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            updateUI(account)
        }

        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired

        if (isLoggedIn) {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile"));
        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) { // result login google
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        } else { // result login facebook
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
//            val account = GoogleSignIn.getLastSignedInAccount(this)
            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            viewModel.getToken(account, this@LoginActivity)
            Log.d(TAG, account.serverAuthCode.toString())
            intent.putExtra(KEY_PHONE_NUMBER, account.email + account.id)
            startActivity(intent)
        } else {
//            Toast.makeText(this@LoginActivity, "Đăng nhập thất bại", Toast.LENGTH_LONG).show()
        }
    }

    // print hash key for login facebook
    fun printHashKey(pContext: Context) {
        try {
            val info: PackageInfo = pContext.getPackageManager()
                .getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey: String = String(Base64.encode(md.digest(), 0))
                Log.i(TAG, "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "printHashKey()", e)
        } catch (e: Exception) {
            Log.e(TAG, "printHashKey()", e)
        }
    }

}