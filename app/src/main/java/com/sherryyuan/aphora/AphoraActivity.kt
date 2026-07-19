package com.sherryyuan.aphora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.sherryyuan.aphora.database.QuoteDao
import com.sherryyuan.aphora.database.SourceDao
import com.sherryyuan.aphora.database.TagDao
import com.sherryyuan.aphora.database.entities.QuoteSourceCrossRef
import com.sherryyuan.aphora.database.entities.QuoteTagCrossRef
import com.sherryyuan.aphora.navigation.AphoraRootNav
import com.sherryyuan.aphora.onboarding.DEFAULT_QUOTE_BUNDLES
import com.sherryyuan.aphora.onboarding.DEFAULT_TAGS
import com.sherryyuan.aphora.ui.theme.AphoraTheme
import com.sherryyuan.aphora.utils.isFirstInstall
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AphoraActivity : ComponentActivity() {

    @Inject
    lateinit var quoteDao: QuoteDao

    @Inject
    lateinit var sourceDao: SourceDao

    @Inject
    lateinit var tagDao: TagDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (isFirstInstall(this)) {
            lifecycleScope.launch {
                seedDefaultData()
            }
        }

        setContent {
            AphoraTheme {
                AphoraRootNav()
            }
        }
    }

    private suspend fun seedDefaultData() {
        val tagIdMap = DEFAULT_TAGS.associate { it.label to tagDao.insertEntity(it) }
        DEFAULT_QUOTE_BUNDLES.forEach { bundle ->
            val quoteId = quoteDao.insertQuote(bundle.quote)
            val sourceId = sourceDao.insertSource(bundle.source)
            quoteDao.insertQuoteSourceCrossRef(QuoteSourceCrossRef(quoteId, sourceId))
            bundle.tags
                .mapNotNull { tagIdMap[it] }
                .forEach { tagId ->
                    quoteDao.insertQuoteTagCrossRef(QuoteTagCrossRef(quoteId, tagId))
                }
        }
    }
}
