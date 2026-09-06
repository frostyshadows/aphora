package com.sherryyuan.aphora

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import javax.inject.Inject

class Analytics @Inject constructor() {

    private val firebaseAnalytics by lazy {
        Firebase.analytics
    }

    fun logEvent(name: String, params: Map<String, String> = emptyMap()) {
        val paramsBundle = if (params.isNotEmpty()) {
            Bundle().apply {
                params.forEach { (key, value) ->
                    putString(key, value)
                }
            }
        } else {
            null
        }
        firebaseAnalytics.logEvent(name, paramsBundle)
    }

    companion object {
        // region event names
        const val EVENT_AD_LOAD_SUCCESS = "Ad load success"
        const val EVENT_AD_LOAD_ERROR = "Ad load error"
        const val EVENT_AD_DISMISSED = "Ad dismissed"
        const val EVENT_AD_FAILED_TO_SHOW = "Ad failed to show"
        const val EVENT_AD_SHOWED_FULLSCREEN_CONTENT = "Ad showed fullscreen content"
        const val EVENT_AD_IMPRESSION = "Ad impression"
        const val EVENT_AD_CLICKED = "Ad clicked"
        const val EVENT_CONSENT_INFO_UPDATE_ERROR = "Consent info update error"
        const val EVENT_CONSENT_FORM_ERROR = "Consent form error"
        // endregion

        // region event param keys
        const val EVENT_KEY_ERROR_MESSAGE = "Error message"
        // endregion
    }
}
