package com.personal.accountantAssistant.extensions

import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.personal.accountantAssistant.databinding.LayoutNativeAdBinding

fun NativeAdView.fillNativeAdView(layout: LayoutNativeAdBinding, nativeAd: NativeAd) {
    with(layout) {
        tvNativeAdTitle.text = nativeAd.headline
        tvNativeAdName.text = nativeAd.advertiser
        ivNativeAdIcon.setImageDrawable(nativeAd.icon?.drawable)
        btnNativeAdAction.text = nativeAd.store
        tvNativeAdDescription.text = nativeAd.body
        headlineView = tvNativeAdTitle
        advertiserView = tvNativeAdName
        iconView = ivNativeAdIcon
        mediaView = mvNativeAdMedia
        callToActionView = btnNativeAdAction
        bodyView = tvNativeAdDescription
        setNativeAd(nativeAd)
    }
}