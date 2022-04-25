package com.personal.accountantAssistant.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.databinding.ActivityLoginBinding
import com.personal.accountantAssistant.di.MainModuleInitializer
import com.personal.accountantAssistant.extensions.closeApp
import com.personal.accountantAssistant.extensions.startMainActivity
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.services.SignInService
import org.koin.android.ext.android.inject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val analytics: AnalyticsProvider? by inject()
    private val service: SignInService? by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainModuleInitializer.initialize()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.signInButton.setOnClickListener { signInChooser() }
    }

    override fun onStart() {
        super.onStart()
        analytics?.trackScreenViewEvent(this::class.simpleName)
    }

    override fun onResume() {
        super.onResume()
        signIn()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (resultCode == RESULT_OK) {
            service?.handleSignInResult(
                resultData, ::startMainActivity, ::onSignInFail
            ) ?: run { onSignInFail() }
        } else onSignInFail()
    }

    override fun onDestroy() {
        super.onDestroy()
        setLoading(false)
    }

    override fun onBackPressed() {
        closeApp()
    }

    private fun setLoading(isLoading: Boolean = true) {
        binding.progressBar.isVisible = isLoading
    }

    private fun setLoginActionVisible(isVisible: Boolean = true) {
        binding.signInButton.isVisible = isVisible
    }

    private fun signInChooser() {
        setLoading()
        service?.requestSignInPicker()?.let {
            startActivityForResult(it, CHOOSER_SIGN_IN_REQUEST_CODE)
        } ?: run { onSignInFail() }
    }

    private fun signIn() {
        setLoading()
        service?.requestSignInAccount()?.let {
            startActivityForResult(it, ACCOUNT_NAME_SIGN_IN_REQUEST_CODE)
        } ?: run { onSignInFail() }
    }

    private fun onSignInFail() {
        setLoading(false)
        setLoginActionVisible()
        analytics?.trackEvent(
            LOGIN_CANCELLED,
            LOGIN_CANCELLED,
            getString(R.string.sign_in_canceled)
        )
    }

    companion object {
        const val ACCOUNT_NAME_SIGN_IN_REQUEST_CODE = 1
        const val CHOOSER_SIGN_IN_REQUEST_CODE = 2
        const val LOGIN_CANCELLED = "login_cancelled"
    }

}