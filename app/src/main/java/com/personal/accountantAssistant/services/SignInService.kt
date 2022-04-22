package com.personal.accountantAssistant.services

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.extensions.startMainActivity
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.providers.CrashlyticsProvider
import com.personal.accountantAssistant.ui.login.LoginActivity

class SignInService(
    private val context: Context,
    private val localStorage: LocalStorage,
    private val analytics: AnalyticsProvider,
    private val crashlytics: CrashlyticsProvider
) {

    private var signInClient: GoogleSignInClient? = null
    private var accountName: String? = localStorage.getSignedAccountName()
    private var scopes: Collection<String> = listOf(DriveScopes.DRIVE)

    private fun getSignInOptionsBuilder(): GoogleSignInOptions.Builder = GoogleSignInOptions
        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestScopes(Scope(DriveScopes.DRIVE))

    /**
     * Request sign in intent from a provided account.
     */
    fun requestAccountNameSignIn(): Intent? {
        Log.d(TAG, "Requesting silent sign-in")
        val signInOptions = accountName?.let {
            getSignInOptionsBuilder().setAccountName(it).build()
        }
        signInClient = signInOptions?.let { GoogleSignIn.getClient(context, it) }
        return signInClient?.signInIntent
    }

    /**
     * Request sign in intent.
     */
    fun requestSignIn(): Intent? {
        Log.d(TAG, "Requesting sign-in picker")
        val signInOptions = getSignInOptionsBuilder().requestEmail().build()
        signInClient = GoogleSignIn.getClient(context, signInOptions)
        return signInClient?.signInIntent
    }

    /**
     * Handles the `result` of a completed sign-in activity initiated from [ ][.requestSignIn].
     */
    fun handleSignInResult(
        result: Intent, requestCode: Int, signInButton: SignInButton?, progressBar: ProgressBar?
    ) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
            .addOnSuccessListener { googleAccount: GoogleSignInAccount ->
                setAccountName(googleAccount.email)

                Log.d(TAG, "Signed in as $accountName")

                val httpTransport = AndroidHttp.newCompatibleTransport()
                val jsonFactory = GsonFactory()
                val credential = GoogleAccountCredential.usingOAuth2(context, scopes)
                credential?.selectedAccount = googleAccount.account
                val appName = context.getString(R.string.app_name)

                drive = Drive.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName(appName)
                    .build()

                //Once time you are sign in
                if (requestCode == LoginActivity.ACCOUNT_NAME_SIGN_IN_REQUEST_CODE) {
                    signInButton?.visibility = View.INVISIBLE
                }
                context.startMainActivity()
            }
            .addOnFailureListener { exception: Exception? ->
                Log.e(TAG, "Unable to sign in.", exception)
                if (requestCode == LoginActivity.ACCOUNT_NAME_SIGN_IN_REQUEST_CODE) {
                    signInButton?.visibility = View.VISIBLE
                }
                progressBar?.visibility = View.GONE
                cleanData()
            }
    }

    private fun signOutResult(): Task<Void>? {
        return signInClient?.signOut()
    }

    fun signOut(): Task<Void>? {
        return signOutResult()
            ?.addOnSuccessListener {
                Log.d(TAG, "Signed out")
                cleanData()
            }
            ?.addOnFailureListener { exception: Exception? ->
                Log.e(TAG, "Unable to sign out.", exception)
            }
    }

    private fun setAccountName(accountName: String?) {
        this.accountName = accountName
        localStorage.setSignedAccountName(accountName)
        analytics.setUserEmail(accountName)
        crashlytics.setUser(accountName)
    }

    private fun cleanData() {
        signInClient = null
        drive = null
        setAccountName(null)
    }

    companion object {
        private val TAG = SignInService::class.java.simpleName
        var drive: Drive? = null
    }
}