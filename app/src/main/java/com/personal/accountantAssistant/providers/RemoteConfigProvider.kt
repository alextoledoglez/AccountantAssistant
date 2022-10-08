package com.personal.accountantAssistant.providers

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.personal.accountantAssistant.BuildConfig
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.extensions.ZERO
import java.util.concurrent.TimeUnit

class RemoteConfigProvider {

    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    private fun getMinimumFetchIntervalInSeconds() = if (BuildConfig.DEBUG)
        Long.ZERO
    else
        TimeUnit.HOURS.toSeconds(FETCH_MINIMUM_DURATION)

    fun init() {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(FETCH_MINIMUM_INTERVAL)
            .build()

        remoteConfig.apply {
            setConfigSettingsAsync(configSettings)
            setDefaultsAsync(R.xml.remote_config_defaults)
            fetch(getMinimumFetchIntervalInSeconds()).addOnCompleteListener {
                if (it.isSuccessful)
                    this.activate()
            }
        }
    }

    fun getBoolean(key: String) = remoteConfig.getBoolean(key)

    fun getString(key: String) = remoteConfig.getString(key)

    fun getByteArray(key: String) = remoteConfig.getValue(key).asByteArray()

    companion object {
        private const val FETCH_MINIMUM_DURATION = 12L
        private const val FETCH_MINIMUM_INTERVAL = 900L
    }
}