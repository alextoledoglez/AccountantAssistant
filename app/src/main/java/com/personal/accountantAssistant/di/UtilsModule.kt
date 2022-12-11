package com.personal.accountantAssistant.di

import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.personal.accountantAssistant.data.LocalStorage
import org.koin.dsl.module
import java.util.concurrent.Executors

object UtilsModule {

    fun getUtils() = module {
        single { GsonFactory() }
        single { LocalStorage(get()) }
        single { MobileAds.initialize(get()) {} }
        single { GoogleSignInOptions.DEFAULT_SIGN_IN }
        single { Executors.newSingleThreadExecutor() }
        single { AndroidHttp.newCompatibleTransport() }
        single { GoogleSignInOptions.Builder(get()) }
        single { GoogleAccountCredential.usingOAuth2(get(), get()) }
    }
}