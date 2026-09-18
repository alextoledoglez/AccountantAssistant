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
        val googleSignInClient = options?.let { GoogleSignIn.getClient(context, it) }
        trackSignInEvent(value = "client_requested")
        return googleSignInClient
    }

    fun getSignInClientBy(email: String? = null): GoogleSignInClient? {
        options = email?.let { builder.setAccountName(it) }?.build()
        trackSignInEvent(value = "account_selected")
        return getGoogleSignInClient()
    }

    fun getSignInClient(): GoogleSignInClient? {
        options = builder.requestEmail().build()
        trackSignInEvent(value = "client_requested")
        return getGoogleSignInClient()
    }

    /**
     * Handles the result of a completed Google sign-in activity.
     */
    fun handleSignInResult(
        result: Intent?,
        onSuccess: ((account: GoogleSignInAccount?) -> Unit)?,
        onFailure: (() -> Unit)? = null
    ) {
        GoogleSignIn.getSignedInAccountFromIntent(result).addOnSuccessListener {
            account = it
            crashlytics?.clearUser()
            trackSignInEvent(value = "success")
            onSuccess?.invoke(it)
        }.addOnFailureListener {
            onFailure?.invoke()
            trackSignInEvent(value = "failure")
        }
    }

    fun signOut(onSuccess: (() -> Unit)? = null) {
        getSignInClientBy(account?.email)?.signOut()?.addOnSuccessListener {
            crashlytics?.clearUser()
            trackSigOutEvent(value = "success")
            account = null
            onSuccess?.invoke()
        }?.addOnFailureListener {
            trackSigOutEvent(value = "failure")
        } ?: run {
            trackSigOutEvent(value = "client_unavailable")
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
