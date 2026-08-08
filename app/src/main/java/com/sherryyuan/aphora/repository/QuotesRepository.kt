package com.sherryyuan.aphora.repository

import com.sherryyuan.aphora.database.QuoteDao
import com.sherryyuan.aphora.database.entities.QuoteDbModel
import com.sherryyuan.aphora.database.entities.QuoteEntity
import com.sherryyuan.aphora.database.entities.QuoteSourceCrossRef
import com.sherryyuan.aphora.database.entities.QuoteTagCrossRef
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QuotesRepository @Inject constructor(private val quoteDao: QuoteDao) {

    fun getQuotes(): Flow<List<QuoteDbModel>> {
        return quoteDao.getAll()
    }

    suspend fun getQuoteById(id: Long): QuoteDbModel? {
        return quoteDao.getQuoteById(id)
    }

    suspend fun saveQuote(
        existingQuoteId: Long?,
        quoteText: String,
        rating: Int,
        sourceId: Long?,
        tagIds: List<Long>,
        noteText: String?,
    ) {
        val quoteId = if (existingQuoteId != null) {
            val existingQuote = quoteDao.getQuoteById(existingQuoteId)
            quoteDao.insertQuote(
                quote = QuoteEntity(
                    quoteId = existingQuoteId,
                    text = quoteText,
                    rating = rating,
                    userNote = noteText,
                    timestampAdded = existingQuote?.quote?.timestampAdded
                        ?: System.currentTimeMillis(),
                    timestampLastEdited = System.currentTimeMillis(),
                )
            )
        } else {
            quoteDao.insertQuote(
                quote = QuoteEntity(
                    text = quoteText,
                    rating = rating,
                    userNote = noteText,
                    timestampAdded = System.currentTimeMillis(),
                    timestampLastEdited = System.currentTimeMillis(),
                )
            )
        }
        sourceId?.let {
            quoteDao.insertQuoteSourceCrossRef(QuoteSourceCrossRef(quoteId, it))
        }
        tagIds.forEach { tagId ->
            quoteDao.insertQuoteTagCrossRef(QuoteTagCrossRef(quoteId, tagId))
        }
    }

    suspend fun deleteQuote(quoteId: Long) {
        quoteDao.deleteQuote(quoteId)
    }
}
