package com.sherryyuan.aphora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.sherryyuan.aphora.ads.AdsRepository
import com.sherryyuan.aphora.database.QuoteDao
import com.sherryyuan.aphora.database.SourceDao
import com.sherryyuan.aphora.database.TagDao
import com.sherryyuan.aphora.navigation.AphoraRootNav
import com.sherryyuan.aphora.navigation.Navigator
import com.sherryyuan.aphora.onboarding.DEFAULT_QUOTE_BUNDLES
import com.sherryyuan.aphora.onboarding.DEFAULT_TAGS
import com.sherryyuan.aphora.ui.theme.AphoraTheme
import com.sherryyuan.aphora.utils.isFirstInstall
import com.sherryyuan.aphora.utils.markFirstInstallComplete
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@AndroidEntryPoint
class AphoraActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var adsRepository: AdsRepository

    @Inject
    lateinit var analytics: Analytics

    @Inject
    lateinit var quoteDao: QuoteDao

    @Inject
    lateinit var sourceDao: SourceDao

    @Inject
    lateinit var tagDao: TagDao

    private lateinit var consentInformation: ConsentInformation

    private val adsInitialized = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        maybeShowUMPConsentForm()

        if (isFirstInstall(this)) {
            lifecycleScope.launch {
                seedDefaultData()
                markFirstInstallComplete(this@AphoraActivity)
            }
        }

        setContent {
            AphoraTheme {
                AphoraRootNav(
                    navigator = navigator,
                    onQuoteSaved = {
                        if (consentInformation.canRequestAds()) {
                            adsRepository.maybeShowInterstitial()
                        }
                    }
                )
            }
        }
    }

    private fun maybeShowUMPConsentForm() {
        // Testing instructions: https://developers.google.com/admob/android/privacy?consent=legacy#testing
        val debugSettings = ConsentDebugSettings.Builder(this)
            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
            .addTestDeviceHashedId("TEST-DEVICE-HASHED-ID")
            .build()

        val params = ConsentRequestParameters
            .Builder()
            // .setConsentDebugSettings(debugSettings)
            .build()

        consentInformation = UserMessagingPlatform.getConsentInformation(this)
        consentInformation.requestConsentInfoUpdate(
            this,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(this) { formError ->
                    if (formError != null) {
                        analytics.logEvent(
                            name = Analytics.EVENT_CONSENT_FORM_ERROR,
                            params = mapOf(Analytics.EVENT_KEY_ERROR_MESSAGE to formError.message),
                        )
                    }
                    if (consentInformation.canRequestAds()) {
                        initializeAdsIfNeeded()
                    }
                }
            },
            { requestConsentError ->
                analytics.logEvent(
                    name = Analytics.EVENT_CONSENT_INFO_UPDATE_ERROR,
                    params = mapOf(Analytics.EVENT_KEY_ERROR_MESSAGE to requestConsentError.message),
                )
            },
        )
    }

    /**
     * The ads SDK may preload ads as soon as it is initialized, so this is only called once UMP
     * consent has been resolved.
     */
    private fun initializeAdsIfNeeded() {
        if (adsInitialized.getAndSet(true)) return
        lifecycleScope.launch(Dispatchers.IO) {
            MobileAds.initialize(this@AphoraActivity)
            withContext(Dispatchers.Main) {
                adsRepository.refreshInterstitial()
            }
        }
    }

    private suspend fun seedDefaultData() {
        val tagIdMap = DEFAULT_TAGS.associate { it.label to tagDao.insertEntity(it) }
        DEFAULT_QUOTE_BUNDLES.forEach { bundle ->
            val sourceId = sourceDao.insertSource(bundle.source)
            quoteDao.upsertQuoteWithRelations(
                quote = bundle.quote,
                sourceId = sourceId,
                tagIds = bundle.tags.mapNotNull { tagIdMap[it] },
            )
        }
    }
}
