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
import com.personal.accountantAssistant.ui.login.LoginActivity
import com.personal.accountantAssistant.utils.ActivityUtils
import java.util.*

class SignInService(val context: Context) {

    private var driveService: Drive? = null
    private var localStorage: LocalStorage? = null
    private var signInClient: GoogleSignInClient? = null
    private var signInOptions: GoogleSignInOptions? = null
    private var credential: GoogleAccountCredential? = null
    private var accountName: String? = null
    private var scopes: Collection<String> = Collections.singleton(DriveScopes.DRIVE_FILE)

    companion object {
        private val TAG = SignInService::class.java.simpleName
    }

    init {
        localStorage = LocalStorage(context)
        accountName = localStorage?.getSignedAccountName()
    }

    private fun getSignInOptionsBuilder(): GoogleSignInOptions.Builder {
        return GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Scope(DriveScopes.DRIVE_FILE))
    }

    private fun getSignInClient(signInOptions: GoogleSignInOptions?): GoogleSignInClient? {
        signInClient = signInOptions?.let { GoogleSignIn.getClient(context, it) }
        return signInClient
    }

    private fun getSignInClientIntent(signInOptions: GoogleSignInOptions?): Intent? {
        return getSignInClient(signInOptions)?.signInIntent
    }

    /**
     * Request sign in intent from a provided account.
     */
    fun getAccountNameSignInClient(): GoogleSignInClient? {
        Log.d(TAG, "Requesting silent sign-in")
        signInOptions = accountName?.let { getSignInOptionsBuilder().setAccountName(it).build() }
        return getSignInClient(signInOptions)
    }

    /**
     * Request sign in intent from a provided account.
     */
    fun requestAccountNameSignIn(): Intent? {
        return getAccountNameSignInClient()?.signInIntent
    }

    /**
     * Request sign in intent.
     */
    fun requestSignIn(): Intent? {
        Log.d(TAG, "Requesting sign-in picker")
        signInOptions = getSignInOptionsBuilder().requestEmail().build()
        return getSignInClientIntent(signInOptions)
    }

    /**
     * Handles the `result` of a completed sign-in activity initiated from [ ][.requestSignIn].
     */
    fun handleSignInResult(result: Intent, requestCode: Int, signInButton: SignInButton?, progressBar: ProgressBar?) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener { googleAccount: GoogleSignInAccount ->
                    setAccountName(googleAccount.email)
                    Log.d(TAG, "Signed in as $accountName")
                    // Use the authenticated account to sign in to the Drive service.
                    credential = GoogleAccountCredential.usingOAuth2(context, scopes)
                    credential?.selectedAccount = googleAccount.account
                    driveService = Drive.Builder(AndroidHttp.newCompatibleTransport(), GsonFactory(), credential)
                            .setApplicationName(context.getString(R.string.app_name))
                            .build()
                    //Once time you are sign in
                    if (requestCode == LoginActivity.ACCOUNT_NAME_SIGN_IN_REQUEST_CODE) {
                        signInButton?.visibility = View.INVISIBLE
                    }
                    ActivityUtils.startMainActivity(context)
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

    fun signOutResult(): Task<Void>? {
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
        localStorage?.setSignedAccountName(this.accountName)
    }

    private fun cleanData() {
        signInClient = null
        driveService = null
        setAccountName(null)
    }
}