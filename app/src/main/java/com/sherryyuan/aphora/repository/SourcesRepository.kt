package com.sherryyuan.aphora.repository

import com.sherryyuan.aphora.database.SourceDao
import com.sherryyuan.aphora.database.entities.SourceCategory
import com.sherryyuan.aphora.database.entities.SourceEntity
import com.sherryyuan.aphora.savedQuotes.QuoteUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SourcesRepository @Inject constructor(private val sourceDao: SourceDao) {

    fun getAllSources(): Flow<List<SourceEntity>> {
        return sourceDao.getAllSources()
    }

    fun getAllAuthors(): Flow<List<String>> {
        return sourceDao.getAllAuthors()
    }

    suspend fun saveSource(
        source: QuoteUiModel.Source,
    ): Long {
        val allSources = sourceDao.getAllSources().first()
        val existingSource = allSources.firstOrNull { existingSource ->
            source.author.equals(existingSource.author, ignoreCase = true) &&
                    source.work.equals(existingSource.work, ignoreCase = true) &&
                    source.category == existingSource.category
        }
        return if (existingSource != null) {
            existingSource.sourceId
        } else {
            val newSource = SourceEntity(
                author = source.author,
                work = source.work,
                category = source.category ?: SourceCategory.OTHER,
            )
            sourceDao.insertSource(newSource)
        }
    }
}
