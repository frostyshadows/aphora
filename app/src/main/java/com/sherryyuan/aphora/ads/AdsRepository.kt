package com.sherryyuan.aphora.ads

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.admob.loadAndTrackInterstitialAd
import com.sherryyuan.aphora.Analytics
import com.sherryyuan.aphora.BuildConfig
import com.sherryyuan.aphora.PREFS_NAME
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class AdsRepository @Inject constructor(
    private val activity: Activity,
    private val analytics: Analytics,
) {
    private var currentInterstitialAd: InterstitialAd? = null

    private val sharedPrefs: SharedPreferences
        get() = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun refreshInterstitial() {
        Purchases.sharedInstance.adTracker.loadAndTrackInterstitialAd(
            context = activity,
            adUnitId = if (BuildConfig.DEBUG) {
                AD_ID_INTERSTITIAL_TEST
            } else {
                AD_ID_INTERSTITIAL_AFTER_QUOTE_ADD
            },
            adRequest = AdRequest.Builder().build(),
            loadCallback = object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    analytics.logEvent(Analytics.EVENT_AD_LOAD_SUCCESS)
                    currentInterstitialAd = ad
                    currentInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                analytics.logEvent(Analytics.EVENT_AD_DISMISSED)
                                refreshInterstitial()
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                analytics.logEvent(
                                    name = Analytics.EVENT_AD_FAILED_TO_SHOW,
                                    params = mapOf(Analytics.EVENT_KEY_ERROR_MESSAGE to adError.message),
                                )
                                refreshInterstitial()
                            }

                            override fun onAdShowedFullScreenContent() {
                                sharedPrefs.edit {
                                    putInt(PREFS_INTERSTITIAL_SKIPPED_COUNT_KEY, 0)
                                }
                                if (!sharedPrefs.getBoolean(PREFS_FIRST_INTERSTITIAL_SEEN_KEY, false)) {
                                    sharedPrefs.edit {
                                        putBoolean(PREFS_FIRST_INTERSTITIAL_SEEN_KEY, true)
                                    }
                                }
                                analytics.logEvent(Analytics.EVENT_AD_SHOWED_FULLSCREEN_CONTENT)
                            }

                            override fun onAdImpression() {
                                analytics.logEvent(Analytics.EVENT_AD_IMPRESSION)
                            }

                            override fun onAdClicked() {
                                analytics.logEvent(Analytics.EVENT_AD_CLICKED)
                            }
                        }
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    analytics.logEvent(
                        name = Analytics.EVENT_AD_LOAD_ERROR,
                        params = mapOf(Analytics.EVENT_KEY_ERROR_MESSAGE to adError.message),
                    )
                    currentInterstitialAd = null
                }
            },
        )
    }

    fun maybeShowInterstitial() {
        if (shouldShowInterstitial()) {
            currentInterstitialAd?.show(activity)
        } else {
            val currentSkips = sharedPrefs.getInt(PREFS_INTERSTITIAL_SKIPPED_COUNT_KEY, 0)
            sharedPrefs.edit {
                putInt(PREFS_INTERSTITIAL_SKIPPED_COUNT_KEY, currentSkips + 1)
            }
        }
    }

    private fun shouldShowInterstitial(): Boolean {
        val hasSeenInterstitial = sharedPrefs.getBoolean(PREFS_FIRST_INTERSTITIAL_SEEN_KEY, false)
        val maxSkips = if (hasSeenInterstitial) {
            SKIPS_BEFORE_SHOWING_INTERSTITIAL
        } else {
            SKIPS_BEFORE_SHOWING_FIRST_INTERSTITIAL
        }
        val currentSkips = sharedPrefs.getInt(PREFS_INTERSTITIAL_SKIPPED_COUNT_KEY, 0)
        return currentSkips > maxSkips
    }

    companion object {
        private const val AD_ID_INTERSTITIAL_AFTER_QUOTE_ADD = "ca-app-pub-2704548882765332/3427489519"
        // Configured to return test ads for every request, use it for testing
        private const val AD_ID_INTERSTITIAL_TEST = "ca-app-pub-3940256099942544/1033173712"

        private const val SKIPS_BEFORE_SHOWING_FIRST_INTERSTITIAL = 10
        private const val SKIPS_BEFORE_SHOWING_INTERSTITIAL = 5
        private const val PREFS_FIRST_INTERSTITIAL_SEEN_KEY = "interstitial_seen"
        private const val PREFS_INTERSTITIAL_SKIPPED_COUNT_KEY = "interstitial_skipped_count"
    }
}
