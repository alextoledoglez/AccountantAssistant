package com.personal.accountantAssistant.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.databinding.ActivityLoginBinding
import com.personal.accountantAssistant.di.MainModuleInitializer
import com.personal.accountantAssistant.extensions.ZERO
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.startMainActivity
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.services.SignInService
import org.koin.android.ext.android.inject
import kotlin.system.exitProcess

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val signInService: SignInService? by inject()
    private val analytics: AnalyticsProvider? by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainModuleInitializer.initialize()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.signInButton.setOnClickListener { signIn() }
    }

    override fun onStart() {
        super.onStart()
        analytics?.trackScreenViewEvent(this::class.simpleName)
    }

    override fun onResume() {
        super.onResume()
        setLoading()
        startActivityForResult(
            signInService?.requestAccountNameSignIn(), ACCOUNT_NAME_SIGN_IN_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (resultCode == RESULT_OK) {
            val wasSuccessful = signInService?.handleSignInResult(resultData).orFalse()
            binding.signInButton.isVisible = wasSuccessful
            if (wasSuccessful) startMainActivity() else setLoading(false)
        } else {
            setLoading(false)
            binding.signInButton.isVisible = (requestCode == ACCOUNT_NAME_SIGN_IN_REQUEST_CODE)
            analytics?.trackEvent(
                LOGIN_CANCELLED,
                LOGIN_CANCELLED,
                getString(R.string.sign_in_canceled)
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        setLoading(false)
    }

    override fun onBackPressed() {
        finishAffinity()
        exitProcess(Int.ZERO)
    }

    private fun signIn() {
        setLoading()
        startActivityForResult(signInService?.requestSignIn(), CHOOSER_SIGN_IN_REQUEST_CODE)
    }

    private fun setLoading(isLoading: Boolean = true) {
        binding.progressBar.isVisible = isLoading
    }

    companion object {
        const val ACCOUNT_NAME_SIGN_IN_REQUEST_CODE = 1
        const val CHOOSER_SIGN_IN_REQUEST_CODE = 2
        const val LOGIN_CANCELLED = "login_cancelled"
    }

}