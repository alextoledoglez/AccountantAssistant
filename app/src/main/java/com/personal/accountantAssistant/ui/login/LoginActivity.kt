package com.personal.accountantAssistant.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.SignInButton
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.services.SignInService
import com.personal.accountantAssistant.utils.ToastUtils

class LoginActivity : AppCompatActivity() {

    private val REQUEST_CODE_SIGN_IN = 1
    private var signInService: SignInService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        initializeSignInService()

        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        signInButton.setSize(SignInButton.SIZE_WIDE)

        signInButton.setOnClickListener {
            signInService?.requestSignIn().apply { startActivityForResult(this, REQUEST_CODE_SIGN_IN) }
        }
    }

    override fun onStart() {
        super.onStart()
        initializeSignInService()
    }

    override fun onResume() {
        super.onResume()
        initializeSignInService()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        when (requestCode) {
            REQUEST_CODE_SIGN_IN -> if (resultCode == RESULT_OK) {
                resultData?.let { signInService?.handleSignInResult(it) }
            } else {
                ToastUtils.showLongText(applicationContext, R.string.sign_in_canceled)
            }
        }
    }

    private fun initializeSignInService() {
        signInService = signInService ?: run {
            SignInService(applicationContext)
        }
        signInService?.requestAccountNameSignIn().apply { startActivityForResult(this, REQUEST_CODE_SIGN_IN) }
    }
}