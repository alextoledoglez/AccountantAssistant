package com.personal.accountantAssistant.providers

import android.content.Context
import android.widget.FrameLayout
import com.google.android.gms.ads.*
import com.personal.accountantAssistant.BuildConfig

class AdProvider(val context: Context, val analytics: AnalyticsProvider) {

    private val adView = AdView(context).apply {
        setAdSize(AdSize.LARGE_BANNER)
        adUnitId = BuildConfig.admobUnitId
        adListener = object : AdListener() {
            override fun onAdClicked() {
                super.onAdClicked()
                trackAdEvent(AD_CLICKED_KEY, event = "AD was clicked.")
            }

            override fun onAdImpression() {
                super.onAdImpression()
                trackAdEvent(AD_IMPRESSION_KEY, event = "AD impression.")
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                trackAdEvent(AD_LOADED_KEY, event = "AD was loaded.")
            }

            override fun onAdClosed() {
                super.onAdClosed()
                trackAdEvent(AD_CLOSED_KEY, event = "AD was closed.")
            }

            override fun onAdOpened() {
                super.onAdOpened()
                trackAdEvent(AD_OPENED_KEY, event = "AD was opened.")
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                analytics.trackErrorEvent(
                    error = "Failed to load ad : 'code: ${error.code}, message: ${error.message}'"
                )
            }
        }
    }

    init {
        if (BuildConfig.admobTestDeviceId.isNotBlank()) {
            val config = RequestConfiguration.Builder().setTestDeviceIds(
                listOf(BuildConfig.admobTestDeviceId)
            ).build()
            MobileAds.setRequestConfiguration(config)
        }
    }

    private fun trackAdEvent(key: String = AD_KEY, event: String?) {
        analytics.trackEvent(key, AD_MESSAGE_KEY, event.orEmpty())
    }

    fun loadAdOn(container: FrameLayout) {
        with(container) {
            removeAllViews()
            addView(adView)
        }
        adView.loadAd(AdRequest.Builder().build())
    }

    fun destroyAd() {
        adView.destroy()
    }

    fun pauseAd() {
        adView.pause()
    }

    fun resumeAd() {
        adView.resume()
    }

    companion object {
        const val AD_KEY = "AD_KEY"
        const val AD_MESSAGE_KEY = "AD_MESSAGE_KEY"
        const val AD_LOADED_KEY = "AD_LOADED_KEY"
        const val AD_CLOSED_KEY = "AD_CLOSED_KEY"
        const val AD_OPENED_KEY = "AD_OPENED_KEY"
        const val AD_CLICKED_KEY = "AD_CLICKED_KEY"
        const val AD_IMPRESSION_KEY = "AD_IMPRESSION_KEY"
    }
}