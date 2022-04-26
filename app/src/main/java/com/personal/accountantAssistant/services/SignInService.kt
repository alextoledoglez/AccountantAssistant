package com.personal.accountantAssistant.services

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.extensions.takeIfNotBlank
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.providers.CrashlyticsProvider

class SignInService(
    private val context: Context,
    private val analytics: AnalyticsProvider?,
    private val crashlytics: CrashlyticsProvider?
) {

    private var client: GoogleSignInClient? = null
    private var account: GoogleSignInAccount? = null
    private var scopes: Collection<String> = listOf(DriveScopes.DRIVE)

    private fun getSignInOptionsBuilder(): GoogleSignInOptions.Builder = GoogleSignInOptions
        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestScopes(Scope(DriveScopes.DRIVE))

    /**
     * Request sign in intent from a provided account.
     */
    fun requestSignInAccountName(email: String?): Intent? {
        trackSignEvent(SIGN_IN_KEY, "Requesting silent sign-in")
        val options = email.takeIfNotBlank()?.let {
            getSignInOptionsBuilder().setAccountName(it).build()
        }
        client = options?.let { GoogleSignIn.getClient(context, it) }
        return client?.signInIntent
    }

    /**
     * Request sign in intent.
     */
    fun requestSignInPicker(): Intent? {
        trackSignEvent(SIGN_IN_KEY, "Requesting sign-in picker")
        val options = getSignInOptionsBuilder().requestEmail().build()
        client = GoogleSignIn.getClient(context, options)
        return client?.signInIntent
    }

    /**
     * Handles the `result` of a completed sign-in activity initiated from [ ][.requestSignIn].
     */
    fun handleSignInResult(
        result: Intent?,
        onSuccess: ((account: GoogleSignInAccount?) -> Unit)?,
        onFailure: (() -> Unit)? = null
    ) {
        GoogleSignIn.getSignedInAccountFromIntent(result).addOnSuccessListener {
            val httpTransport = AndroidHttp.newCompatibleTransport()
            val jsonFactory = GsonFactory()
            val credential = GoogleAccountCredential.usingOAuth2(context, scopes)
            credential?.selectedAccount = it.account
            drive = Drive.Builder(
                httpTransport, jsonFactory, credential
            ).setApplicationName(context.getString(R.string.app_name)).build()
            updateAccount(it)
            onSuccess?.invoke(it)
        }.addOnFailureListener { exception: Exception? ->
            onFailure?.invoke()
            trackSignEvent(SIGN_OUT_KEY, value = "Unable to sign out: ${exception?.message}")
        }
    }

    fun signOut(onSuccess: (() -> Unit)? = null) {
        client?.signOut()?.addOnSuccessListener {
            updateAccount()
            onSuccess?.invoke()
        }?.addOnFailureListener { exception: Exception? ->
            trackSignEvent(SIGN_OUT_KEY, value = "Unable to sign out: ${exception?.message}.")
        } ?: run {
            trackSignEvent(SIGN_OUT_KEY, value = "Unable to sign out: Null signIn client.")
        }
    }

    private fun updateAccount(account: GoogleSignInAccount? = null) {
        this.account = account
        val email = account?.email.orEmpty()
        crashlytics?.setUser(email)
        trackSignEvent(SIGN_IN_KEY, value = "Signed in as: '$email'.")
    }

    private fun trackSignEvent(key: String, value: String) {
        analytics?.trackEvent(key, key, value)
    }

    companion object {
        var drive: Drive? = null
        const val SIGN_IN_KEY = "SIGN_IN_KEY"
        const val SIGN_OUT_KEY = "SIGN_OUT_KEY"
    }
}