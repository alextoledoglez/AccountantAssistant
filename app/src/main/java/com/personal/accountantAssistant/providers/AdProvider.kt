package com.personal.accountantAssistant.providers

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.personal.accountantAssistant.BuildConfig
import com.personal.accountantAssistant.databinding.LayoutNativeAdBinding
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.extensions.toLayoutInflater

class AdProvider(val context: Context, val analytics: AnalyticsProvider) {

    private var currentAd: NativeAd? = null
    private val layoutInflater = context.toLayoutInflater()
    private val layout: LayoutNativeAdBinding = LayoutNativeAdBinding.inflate(layoutInflater)

    init {
        if (BuildConfig.ADMOB_TEST_DEVICE_ID.isNotBlank()) {
            val config = RequestConfiguration.Builder().setTestDeviceIds(
                listOf(BuildConfig.ADMOB_TEST_DEVICE_ID)
            ).build()
            MobileAds.setRequestConfiguration(config)
        }
    }

    fun loadOn(container: FrameLayout) {

        val videoOptions = VideoOptions.Builder().setStartMuted(false).build()
        val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()

        AdLoader.Builder(context, BuildConfig.ADMOB_UNIT_ID)
            .forNativeAd { ad ->
                currentAd?.destroy()
                fillNativeAdLayoutBy(ad)
                currentAd = ad
                layout.root.setNativeAd(ad)
                with(container) {
                    removeAllViews()
                    addView(layout.root)
                }
            }
            .withNativeAdOptions(adOptions).withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    analytics.trackErrorEvent(
                        error = "Failed to load native ad : 'code: ${error.code}, message: ${error.message}'"
                    )
                }
            })
            .build().loadAd(AdRequest.Builder().build())

    }

    private fun fillNativeAdLayoutBy(ad: NativeAd) {
        with(layout) {
            adHeadline.text = ad.headline
            ad.mediaContent?.let { adMedia.setMediaContent(it) }

            if (ad.body == null) {
                adBody.visibility = View.INVISIBLE
            } else {
                adBody.visibility = View.VISIBLE
                adBody.text = ad.body
            }

            if (ad.callToAction == null) {
                adCallToAction.visibility = View.INVISIBLE
            } else {
                adCallToAction.visibility = View.VISIBLE
                adCallToAction.text = ad.callToAction
            }

            if (ad.icon == null) {
                adAppIcon.visibility = View.GONE
            } else {
                adAppIcon.setImageDrawable(ad.icon?.drawable)
                adAppIcon.visibility = View.VISIBLE
            }

            if (ad.price == null) {
                adPrice.visibility = View.INVISIBLE
            } else {
                adPrice.visibility = View.VISIBLE
                adPrice.text = ad.price
            }

            if (ad.store == null) {
                adStore.visibility = View.INVISIBLE
            } else {
                adStore.visibility = View.VISIBLE
                adStore.text = ad.store
            }

            if (ad.starRating == null) {
                adStars.visibility = View.INVISIBLE
            } else {
                adStars.rating = ad.starRating?.toFloat().orZero()
                adStars.visibility = View.VISIBLE
            }

            if (ad.advertiser == null) {
                adAdvertiser.visibility = View.INVISIBLE
            } else {
                adAdvertiser.text = ad.advertiser
                adAdvertiser.visibility = View.VISIBLE
            }
        }

        ad.mediaContent?.videoController?.let {
            if (it.hasVideoContent().orFalse()) {
                it.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
                    override fun onVideoEnd() {
                        super.onVideoEnd()
                        trackAdEvent(AD_VIDEO_STATUS_KEY, event = "Video playback has ended.")
                    }
                }
            } else
                trackAdEvent(AD_VIDEO_STATUS_KEY, event = "Ad does not contain a video asset.")
        }
    }

    fun trackAdEvent(key: String = AD_KEY, event: String?) {
        analytics.trackEvent(key, AD_MESSAGE_KEY, event.orEmpty())
    }


    fun destroyCurrentAd() {
        currentAd?.destroy()
    }

    companion object {
        const val AD_KEY = "AD_KEY"
        const val AD_MESSAGE_KEY = "AD_MESSAGE_KEY"
        const val AD_VIDEO_STATUS_KEY = "AD_VIDEO_STATUS_KEY"
    }
}