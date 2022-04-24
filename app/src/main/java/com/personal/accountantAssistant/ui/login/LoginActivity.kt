package com.personal.accountantAssistant.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.databinding.ActivityLoginBinding
import com.personal.accountantAssistant.di.MainModuleInitializer
import com.personal.accountantAssistant.extensions.ZERO
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.services.SignInService
import org.koin.android.ext.android.inject
import kotlin.system.exitProcess

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val signInService: SignInService by inject()
    private val analytics: AnalyticsProvider? by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainModuleInitializer.initialize()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.apply {
            signInButton.apply {
                setOnClickListener {
                    progressBar.visibility = View.VISIBLE
                    signInService.requestSignIn()?.let {
                        startActivityForResult(it, CHOOSER_SIGN_IN_REQUEST_CODE)
                    } ?: run { progressBar.visibility = View.GONE }
                }
            }
        }
        signInService.signOut()
    }

    override fun onStart() {
        super.onStart()
        analytics?.trackScreenViewEvent(this::class.simpleName)
    }

    override fun onResume() {
        super.onResume()
        signInService.let { service ->
            binding.progressBar.visibility = View.VISIBLE
            service.requestAccountNameSignIn()
                ?.let { startActivityForResult(it, ACCOUNT_NAME_SIGN_IN_REQUEST_CODE) }
                ?: run {
                    binding.signInButton.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (resultCode == RESULT_OK) {
            resultData?.let {
                signInService.handleSignInResult(
                    it, requestCode, binding.signInButton, binding.progressBar
                )
            }
        } else {
            binding.progressBar.visibility = View.GONE
            analytics?.trackEvent(
                LOGIN_CANCELLED,
                LOGIN_CANCELLED,
                getString(R.string.sign_in_canceled)
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.progressBar.visibility = View.GONE
    }

    override fun onBackPressed() {
        finishAffinity()
        exitProcess(Int.ZERO)
    }

    companion object {
        const val ACCOUNT_NAME_SIGN_IN_REQUEST_CODE = 1
        const val CHOOSER_SIGN_IN_REQUEST_CODE = 2
        const val LOGIN_CANCELLED = "login_cancelled"
    }

}