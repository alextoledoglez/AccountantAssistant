package com.personal.accountantAssistant.services

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.data.mappers.toUserModel
import com.personal.accountantAssistant.extensions.takeIfNotBlank
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.providers.CrashlyticsProvider

class SignInService(
    private val context: Context,
    private val localStorage: LocalStorage?,
    private val analytics: AnalyticsProvider?,
    private val crashlytics: CrashlyticsProvider?
) {

    private var accountEmail: String? = null
    private var signInClient: GoogleSignInClient? = null
    private var scopes: Collection<String> = listOf(DriveScopes.DRIVE)

    private fun getSignInOptionsBuilder(): GoogleSignInOptions.Builder = GoogleSignInOptions
        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestScopes(Scope(DriveScopes.DRIVE))

    /**
     * Request sign in intent from a provided account.
     */
    fun requestAccountNameSignIn(): Intent? {
        trackSignEvent(SIGN_IN_KEY, "Requesting silent sign-in")
        accountEmail = localStorage?.getSignedAccountName()
        val options = accountEmail?.takeIfNotBlank()?.let {
            getSignInOptionsBuilder().setAccountName(it).build()
        }
        signInClient = options?.let { GoogleSignIn.getClient(context, it) }
        return signInClient?.signInIntent
    }

    /**
     * Request sign in intent.
     */
    fun requestSignIn(): Intent? {
        trackSignEvent(SIGN_IN_KEY, "Requesting sign-in picker")
        val options = getSignInOptionsBuilder().requestEmail().build()
        signInClient = GoogleSignIn.getClient(context, options)
        return signInClient?.signInIntent
    }

    /**
     * Handles the `result` of a completed sign-in activity initiated from [ ][.requestSignIn].
     */
    fun handleSignInResult(result: Intent?): Boolean {
        return GoogleSignIn.getSignedInAccountFromIntent(result)
            .addOnSuccessListener { googleAccount: GoogleSignInAccount ->
                setAccount(googleAccount)
                val httpTransport = AndroidHttp.newCompatibleTransport()
                val jsonFactory = GsonFactory()
                val credential = GoogleAccountCredential.usingOAuth2(context, scopes)
                credential?.selectedAccount = googleAccount.account
                drive = Drive.Builder(
                    httpTransport, jsonFactory, credential
                ).setApplicationName(context.getString(R.string.app_name)).build()
            }
            .addOnFailureListener { exception: Exception? ->
                cleanData()
                trackSignEvent(SIGN_OU_KEY, value = "Unable to sign out: ${exception?.message}")
            }.isSuccessful
    }

    fun signOut(onSuccessAction: (() -> Unit)? = null): Task<Void>? {
        return signInClient?.signOut()
            ?.addOnSuccessListener {
                cleanData()
                onSuccessAction?.invoke()
                trackSignEvent(SIGN_OU_KEY, value = "'${account?.email.orEmpty()}' was signed out")
            }
            ?.addOnFailureListener { exception: Exception? ->
                trackSignEvent(SIGN_OU_KEY, value = "Unable to sign out: ${exception?.message}")
            }
    }

    private fun setAccount(account: GoogleSignInAccount?) {
        SignInService.account = account
        accountEmail = account?.email
        localStorage?.setSignedAccountName(accountEmail)
        analytics?.setUserAccount(account?.toUserModel())
        crashlytics?.setUser(accountEmail)
        trackSignEvent(SIGN_IN_KEY, value = "Signed in as: '$accountEmail'")
    }

    private fun cleanData() {
        setAccount(null)
        signInClient = null
        drive = null
    }

    private fun trackSignEvent(key: String, value: String) {
        analytics?.trackEvent(key, key, value)
    }

    companion object {
        const val SIGN_IN_KEY = "sign_in_key"
        const val SIGN_OU_KEY = "sign_out_key"
        var account: GoogleSignInAccount? = null
        var drive: Drive? = null
    }
}