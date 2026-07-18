package com.sherryyuan.aphora.repository

import com.sherryyuan.aphora.database.QuoteDao
import com.sherryyuan.aphora.database.entities.QuoteDbModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QuotesRepository @Inject constructor(private val quoteDao: QuoteDao) {

    fun getQuotes(): Flow<List<QuoteDbModel>> {
        return quoteDao.getAll()
    }

    suspend fun getQuoteById(id: Long): QuoteDbModel? {
        return quoteDao.getQuoteById(id)
    }

    fun saveQuote() {
        // quoteDao.insertQuote()
    }
}
