package com.personal.accountantAssistant.services

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.providers.CrashlyticsProvider

class SignInService(
    private val context: Context,
    private val builder: GoogleSignInOptions.Builder,
    private val analytics: AnalyticsProvider?,
    private val crashlytics: CrashlyticsProvider?
) {

    private var options: GoogleSignInOptions? = null
    private var account: GoogleSignInAccount? = null

    private fun getGoogleSignInClient(): GoogleSignInClient? {
        val accountName = options?.account?.name.orEmpty()
        val googleSignInClient = options?.let { GoogleSignIn.getClient(context, it) }
        val signInIntent = googleSignInClient?.signInIntent.toString()
        trackSignInEvent(value = "optionsAccountName: $accountName and clientSignInIntent: $signInIntent")
        return googleSignInClient
    }

    fun getSignInClientBy(email: String? = null): GoogleSignInClient? {
        options = email?.let { builder.setAccountName(it) }?.build()
        trackSignInEvent(value = "Requesting sign-in AccountName: $email")
        return getGoogleSignInClient()
    }

    fun getSignInClient(): GoogleSignInClient? {
        options = builder.requestEmail().build()
        trackSignInEvent(value = "Requesting sign-in client")
        return getGoogleSignInClient()
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
            account = it
            val email = it?.email.orEmpty()
            crashlytics?.setUser(email)
            trackSignInEvent(value = "Signed in as: '$email'.")
            onSuccess?.invoke(it)
        }.addOnFailureListener { exception: Exception? ->
            onFailure?.invoke()
            trackSigOutEvent(value = "Unable to sign in: ${exception?.message}")
        }
    }

    fun signOut(onSuccess: (() -> Unit)? = null) {
        getSignInClientBy(account?.email)?.signOut()?.addOnSuccessListener {
            trackSigOutEvent(value = "Successfully logout of: '${account?.email}'.")
            account = null
            onSuccess?.invoke()
        }?.addOnFailureListener { exception: Exception? ->
            trackSigOutEvent(value = "Unable to logout: ${exception?.message}.")
        } ?: run {
            trackSigOutEvent(value = "Unable to logout: Null account client.")
        }
    }

    private fun trackSignInEvent(value: String) {
        analytics?.trackEvent(TAG, SIGN_IN_KEY, value)
    }

    private fun trackSigOutEvent(value: String) {
        analytics?.trackEvent(TAG, SIGN_OUT_KEY, value)
    }

    companion object {
        private val TAG: String = SignInService::class.java.simpleName
        private const val SIGN_IN_KEY = "SIGN_IN_KEY"
        private const val SIGN_OUT_KEY = "SIGN_OUT_KEY"
    }
}