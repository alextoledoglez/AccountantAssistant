package com.personal.accountantAssistant.ui.login

import android.app.Activity
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.core.view.isVisible
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.BaseActivity
import com.personal.accountantAssistant.databinding.ActivityLoginBinding
import com.personal.accountantAssistant.di.MainModuleInitializer
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.services.SignInService
import org.koin.android.ext.android.inject

class LoginActivity : BaseActivity<LoginViewModel>() {

    override val binding by viewBinding(ActivityLoginBinding::inflate)
    private val analytics: AnalyticsProvider? by inject()
    private val service: SignInService? by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainModuleInitializer.initialize()
        setContentView(binding.root)
        supportActionBar?.hide()
        analytics?.trackScreenViewEvent(this::class.simpleName)
        binding.signInButton.setOnClickListener { signInPicker() }
        with(viewModel) {
            isProcessing.observe(this@LoginActivity) { setProcessing(it) }
            isNotificationTokenLoaded.observe(this@LoginActivity) { if (it.orFalse()) getUser() }
            isLogged.observe(this@LoginActivity) { if (it.orFalse()) startMainActivity() }
            notificationToken.observe(this@LoginActivity) { saveNotificationToken(it) }
            userEmail.observe(this@LoginActivity) { signIn(it, isLogged.value) }
            getNotificationToken()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        setProcessing(false)
    }

    override fun onBackPressed() {
        closeApp()
    }

    private fun setProcessing(isProcessing: Boolean = true) {
        if (isProcessing)
            setLoginActionVisible(false)
        binding.progressBar.isVisible = isProcessing
    }

    private fun setLoginActionVisible(isVisible: Boolean = true) {
        binding.signInButton.isVisible = isVisible
    }

    private fun signInPicker() {
        setProcessing()
        service?.getSignInClient()?.signInIntent
            ?.let { loginPickerLauncher.launch(it) }
            ?: run { onSignInFail() }
    }

    private fun signIn(email: String?, isLogged: Boolean?) {
        if (email?.isNotBlank().orFalse() && !isLogged.orFalse()) {
            setProcessing()
            service?.getSignInClientBy(email)?.signInIntent
                ?.let { loginAccountLauncher.launch(it) }
                ?: run { onSignInFail() }
        } else
            setLoginActionVisible()
    }

    private fun onSignInResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            service?.handleSignInResult(result.data, viewModel::saveAccount, ::onSignInFail)
                ?: run { onSignInFail() }
        } else
            onSignInFail()
    }

    private fun onSignInFail() {
        setProcessing(false)
        setLoginActionVisible()
        analytics?.trackEvent(
            LOGIN_CANCELLED,
            LOGIN_CANCELLED,
            getString(R.string.sign_in_canceled)
        )
    }

    private var loginPickerLauncher = setActivityForResult(this::onSignInResult)

    private var loginAccountLauncher = setActivityForResult(this::onSignInResult)

    companion object {
        const val LOGIN_CANCELLED = "LOGIN_CANCELLED"
    }
}