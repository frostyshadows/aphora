package com.sherryyuan.aphora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.sherryyuan.aphora.database.QuoteDao
import com.sherryyuan.aphora.database.SourceDao
import com.sherryyuan.aphora.database.entities.QuoteEntity
import com.sherryyuan.aphora.database.entities.QuoteSourceCrossRef
import com.sherryyuan.aphora.database.entities.SourceCategory
import com.sherryyuan.aphora.database.entities.SourceEntity
import com.sherryyuan.aphora.mockData.createQuoteViewModel
import com.sherryyuan.aphora.navigation.AphoraRootNav
import com.sherryyuan.aphora.ui.theme.AphoraTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AphoraActivity : ComponentActivity() {

    @Inject
    lateinit var quoteDao: QuoteDao

    @Inject
    lateinit var sourceDao: SourceDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            // addDummyData()
        }

        setContent {
            AphoraTheme {
                AphoraRootNav()
            }
        }
    }

    private suspend fun addDummyData() {
        val mockQuote1 = createQuoteViewModel()
        val mockQuote2 = createQuoteViewModel(text = "Believe you can and you're halfway there.")

        val quoteId1 = quoteDao.insertQuote(
            QuoteEntity(
                text = mockQuote1.text,
                note = mockQuote1.note ?: "",
                rating = mockQuote1.rating,
                visibility = mockQuote1.visibility
            )
        )
        val sourceId = sourceDao.insertSource(
            SourceEntity(
                author = "Steve Jobs",
                work = "Stanford Commencement Speech",
                category = SourceCategory.OTHER
            )
        )
        quoteDao.insertQuoteSourceCrossRef(QuoteSourceCrossRef(quoteId1, sourceId))

        quoteDao.insertQuote(
            QuoteEntity(
                text = mockQuote2.text,
                note = mockQuote2.note ?: "",
                rating = mockQuote2.rating,
                visibility = mockQuote2.visibility
            )
        )
    }
}
