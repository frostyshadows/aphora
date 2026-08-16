package com.sherryyuan.aphora.repository

import com.sherryyuan.aphora.database.QuoteDao
import com.sherryyuan.aphora.database.SortSelectionDao
import com.sherryyuan.aphora.database.entities.QuoteDbModel
import com.sherryyuan.aphora.database.entities.QuoteEntity
import com.sherryyuan.aphora.database.entities.QuoteSourceCrossRef
import com.sherryyuan.aphora.database.entities.QuoteTagCrossRef
import com.sherryyuan.aphora.database.entities.SortOption
import com.sherryyuan.aphora.database.entities.SortSelectionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuotesRepository @Inject constructor(
    private val quoteDao: QuoteDao,
    private val sortSelectionDao: SortSelectionDao,
) {

    fun getQuotes(): Flow<List<QuoteDbModel>> {
        return combine(
            quoteDao.getAll(),
            sortSelectionDao.getSelection()
        ) { quotes, sort ->
            when (sort?.sortOption) {
                SortOption.MOST_LIKED -> quotes.sortedByDescending { it.quote.rating }
                SortOption.LEAST_LIKED -> quotes.sortedBy { it.quote.rating }
                SortOption.EARLIEST_ADDED -> quotes.sortedBy { it.quote.timestampAdded }
                SortOption.MOST_RECENT_ADDED -> quotes.sortedByDescending { it.quote.timestampAdded }
                SortOption.MOST_RECENT_UPDATED -> quotes.sortedByDescending { it.quote.timestampLastEdited }
                null -> quotes.sortedByDescending { it.quote.timestampLastEdited }
            }
        }
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

    fun getSortSelection(): Flow<SortOption?> {
        return sortSelectionDao.getSelection().map { it?.sortOption }
    }

    suspend fun updateSortSelection(sortOption: SortOption) {
        sortSelectionDao.updateSelection(SortSelectionEntity(sortOption = sortOption))
    }
}
