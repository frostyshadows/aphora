package com.sherryyuan.aphora

import android.app.Application
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AphoraApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Purchases.logLevel = if (BuildConfig.DEBUG) {
            LogLevel.DEBUG
        } else {
            LogLevel.WARN
        }
        val apiKey = if (BuildConfig.DEBUG) {
            REVENUE_CAT_DEBUG_API_KEY
        } else {
            REVENUE_CAT_PROD_API_KEY
        }
        Purchases.configure(PurchasesConfiguration.Builder(this, apiKey).build())
    }
}

private const val REVENUE_CAT_PROD_API_KEY = "goog_zhKPmMfoGyqMZhpgKIAgqzsyYjD"
private const val REVENUE_CAT_DEBUG_API_KEY = "test_kNpefRKALeyJcibzFvCjBjwsbqN"
