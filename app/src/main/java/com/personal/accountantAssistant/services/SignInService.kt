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
import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.data.mappers.toUserModel
import com.personal.accountantAssistant.domain.models.UserModel
import com.personal.accountantAssistant.extensions.takeIfNotBlank
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.providers.CrashlyticsProvider

class SignInService(
    private val context: Context,
    private val localStorage: LocalStorage?,
    private val analytics: AnalyticsProvider?,
    private val crashlytics: CrashlyticsProvider?
) {

    private var signInClient: GoogleSignInClient? = null
    private var user: UserModel? = localStorage?.getSignedUser()
    private var scopes: Collection<String> = listOf(DriveScopes.DRIVE)

    private fun getSignInOptionsBuilder(): GoogleSignInOptions.Builder = GoogleSignInOptions
        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestScopes(Scope(DriveScopes.DRIVE))

    /**
     * Request sign in intent from a provided account.
     */
    fun requestSignInAccount(): Intent? {
        trackSignEvent(SIGN_IN_KEY, "Requesting silent sign-in")
        val options = user?.email.takeIfNotBlank()?.let {
            getSignInOptionsBuilder().setAccountName(it).build()
        }
        signInClient = options?.let { GoogleSignIn.getClient(context, it) }
        return signInClient?.signInIntent
    }

    /**
     * Request sign in intent.
     */
    fun requestSignInPicker(): Intent? {
        trackSignEvent(SIGN_IN_KEY, "Requesting sign-in picker")
        val options = getSignInOptionsBuilder().requestEmail().build()
        signInClient = GoogleSignIn.getClient(context, options)
        return signInClient?.signInIntent
    }

    /**
     * Handles the `result` of a completed sign-in activity initiated from [ ][.requestSignIn].
     */
    fun handleSignInResult(
        result: Intent?, onSuccess: (() -> Unit)? = null, onFailure: (() -> Unit)? = null
    ) {
        GoogleSignIn.getSignedInAccountFromIntent(result).addOnSuccessListener {
            setAccount(it)
            val httpTransport = AndroidHttp.newCompatibleTransport()
            val jsonFactory = GsonFactory()
            val credential = GoogleAccountCredential.usingOAuth2(context, scopes)
            credential?.selectedAccount = it.account
            drive = Drive.Builder(
                httpTransport, jsonFactory, credential
            ).setApplicationName(context.getString(R.string.app_name)).build()
            onSuccess?.invoke()
        }.addOnFailureListener { exception: Exception? ->
            cleanData()
            onFailure?.invoke()
            trackSignEvent(SIGN_OU_KEY, value = "Unable to sign out: ${exception?.message}")
        }
    }

    fun signOut(onSuccessAction: (() -> Unit)? = null) {
        signInClient?.signOut()?.addOnSuccessListener {
            cleanData()
            onSuccessAction?.invoke()
            trackSignEvent(SIGN_OU_KEY, value = "'${user?.email.orEmpty()}' was signed out.")
        }?.addOnFailureListener { exception: Exception? ->
            trackSignEvent(SIGN_OU_KEY, value = "Unable to sign out: ${exception?.message}.")
        } ?: run {
            trackSignEvent(SIGN_OU_KEY, value = "Unable to sign out: Null signIn client.")
        }
    }

    private fun setAccount(account: GoogleSignInAccount?) {
        user = account?.toUserModel()
        localStorage?.setSignedUser(user)
        analytics?.setUserAccount(user)
        val email = user?.email.orEmpty()
        crashlytics?.setUser(email)
        trackSignEvent(SIGN_IN_KEY, value = "Signed in as: '$email'.")
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
        var drive: Drive? = null
    }
}