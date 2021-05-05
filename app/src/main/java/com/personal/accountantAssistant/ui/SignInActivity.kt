package com.personal.accountantAssistant.ui

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.SignInButton
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.services.SignInService
import kotlin.system.exitProcess

open class SignInActivity : AppCompatActivity() {

    companion object {
        private val TAG = SignInActivity::class.java.simpleName
    }

    var signInService: SignInService? = null
    var signInButton: SignInButton? = null
    var progressBar: ProgressBar? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (resultCode == RESULT_OK) {
            resultData?.let { signInService?.handleSignInResult(it, requestCode, signInButton, progressBar) }
        } else {
            progressBar?.visibility = View.GONE
            Log.d(TAG, getString(R.string.sign_in_canceled))
        }
    }

    override fun onResume() {
        super.onResume()
        initializeSignInService()
    }

    override fun onDestroy() {
        super.onDestroy()
        progressBar?.visibility = View.GONE
    }

    override fun onBackPressed() {
        finishAffinity()
        exitProcess(0)
    }

    private fun initializeSignInService() {
        signInService = signInService ?: run { SignInService(applicationContext) }
    }
}