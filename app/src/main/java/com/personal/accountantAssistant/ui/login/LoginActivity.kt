package com.personal.accountantAssistant.ui.login

import android.app.Activity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.services.SignInService
import com.personal.accountantAssistant.ui.theme.AccountantTheme
import com.personal.accountantAssistant.workers.NotificationWorker
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModel()
    private val analytics: AnalyticsProvider? by inject()
    private val service: SignInService? by inject()

    private var isProcessing by mutableStateOf(false)
    private var isSignInVisible by mutableStateOf(false)

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            closeApp()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = this@LoginActivity
        NotificationWorker.setupPeriodicWork(context)
        supportActionBar?.hide()
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        analytics?.trackScreenViewEvent(this::class.simpleName)

        setContent {
            AccountantTheme {
                LoginScreen(
                    isProcessing = isProcessing,
                    isSignInVisible = isSignInVisible,
                    onSignInClick = ::signInPicker
                )
            }
        }

        with(viewModel) {
            isProcessing.observe(context) { setLoginProcessing(it) }
            isNotificationTokenLoaded.observe(context) { if (it.orFalse()) getUser() }
            isLogged.observe(context) { if (it.orFalse()) startMainActivity() }
            errorMessage.observe(context) { context.showToastLongText(it) }
            notificationToken.observe(context) { saveNotificationToken(it) }
            userEmail.observe(context) { signIn(it, isLogged.value) }
            setNotification()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        setLoginProcessing(false)
    }

    private fun setLoginProcessing(processing: Boolean = true) {
        isProcessing = processing
        if (processing) isSignInVisible = false
    }

    private fun setLoginActionVisible(visible: Boolean = true) {
        isSignInVisible = visible
        if (visible) isProcessing = false
    }

    private fun signInPicker() {
        setLoginProcessing()
        service?.getSignInClient()?.signInIntent
            ?.let { loginPickerLauncher.launch(it) }
            ?: run { onSignInFail() }
    }

    private fun signIn(email: String?, isLogged: Boolean?) {
        if (email?.isNotBlank().orFalse() && !isLogged.orFalse()) {
            setLoginProcessing()
            service?.getSignInClientBy(email)?.signInIntent
                ?.let { loginAccountLauncher.launch(it) }
                ?: run { onSignInFail() }
        } else {
            setLoginActionVisible()
        }
    }

    private fun onSignInResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            service?.handleSignInResult(result.data, viewModel::saveAccount, ::onSignInFail)
                ?: run { onSignInFail() }
        } else {
            onSignInFail()
        }
    }

    private fun onSignInFail() {
        setLoginProcessing(false)
        setLoginActionVisible()
        analytics?.trackEvent(
            LOGIN_CANCELLED,
            LOGIN_CANCELLED,
            getString(R.string.sign_in_canceled)
        )
    }

    private val loginPickerLauncher = setActivityForResult(this::onSignInResult)
    private val loginAccountLauncher = setActivityForResult(this::onSignInResult)

    companion object {
        const val LOGIN_CANCELLED = "LOGIN_CANCELLED"
    }
}