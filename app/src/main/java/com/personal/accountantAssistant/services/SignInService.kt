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
        trackSignEvent(SIGN_IN_KEY, "Requesting sign-in client")
        return options?.let { GoogleSignIn.getClient(context, it) }
    }

    fun getSignInClientBy(email: String? = null): GoogleSignInClient? {
        options = email?.let { builder.setAccountName(it) }?.build()
        return getGoogleSignInClient()
    }

    fun getSignInClient(): GoogleSignInClient? {
        options = builder.requestEmail().build()
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
            trackSignEvent(SIGN_IN_KEY, value = "Signed in as: '$email'.")
            onSuccess?.invoke(it)
        }.addOnFailureListener { exception: Exception? ->
            onFailure?.invoke()
            trackSignEvent(SIGN_OUT_KEY, value = "Unable to sign in: ${exception?.message}")
        }
    }

    fun signOut(onSuccess: (() -> Unit)? = null) {
        getSignInClientBy(account?.email)?.signOut()?.addOnSuccessListener {
            trackSignEvent(SIGN_OUT_KEY, value = "Successfully logout of: '${account?.email}'.")
            account = null
            onSuccess?.invoke()
        }?.addOnFailureListener { exception: Exception? ->
            trackSignEvent(SIGN_OUT_KEY, value = "Unable to logout: ${exception?.message}.")
        } ?: run {
            trackSignEvent(SIGN_OUT_KEY, value = "Unable to logout: Null account client.")
        }
    }

    private fun trackSignEvent(key: String, value: String) {
        analytics?.trackEvent(key, key, value)
    }

    companion object {
        const val SIGN_IN_KEY = "SIGN_IN_KEY"
        const val SIGN_OUT_KEY = "SIGN_OUT_KEY"
    }
}