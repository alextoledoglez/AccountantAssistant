package com.personal.accountantAssistant.providers

import android.content.Context
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.personal.accountantAssistant.BuildConfig
import com.personal.accountantAssistant.extensions.asViewGroup

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

    @Composable
    fun rememberFrameLayoutWithLifecycle(): FrameLayout {
        val context = LocalContext.current
        val frameLayout = remember { FrameLayout(context) }
        val lifecycle = LocalLifecycleOwner.current.lifecycle
        DisposableEffect(lifecycle, frameLayout) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> adView.resume()
                    Lifecycle.Event.ON_PAUSE -> adView.pause()
                    Lifecycle.Event.ON_DESTROY -> adView.destroy()
                    else -> {}
                }
            }
            lifecycle.addObserver(observer)
            onDispose { lifecycle.removeObserver(observer) }
        }
        return frameLayout
    }

    fun loadAdOn(container: FrameLayout) {
        val parentViewGroup = adView.parent?.asViewGroup()
        if (parentViewGroup != container) {
            parentViewGroup?.removeView(adView)
            with(container) {
                removeAllViews()
                addView(adView)
            }
            adView.loadAd(AdRequest.Builder().build())
        }
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