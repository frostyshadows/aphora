package com.sherryyuan.aphora.repository

import com.sherryyuan.aphora.database.SourceDao
import com.sherryyuan.aphora.database.entities.SourceCategory
import com.sherryyuan.aphora.database.entities.SourceEntity
import com.sherryyuan.aphora.savedQuotes.SourceUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SourcesRepository @Inject constructor(private val sourceDao: SourceDao) {

    fun getAllSources(): Flow<List<SourceEntity>> {
        return sourceDao.getAllSources()
    }

    suspend fun saveSource(
        source: SourceUiModel,
    ): Long {
        val allSources = sourceDao.getAllSources().first()
        val existingSource = allSources.firstOrNull { existingSource ->
            source.writer.equals(existingSource.writer, ignoreCase = true) &&
                    source.work.equals(existingSource.work, ignoreCase = true) &&
                    source.category == existingSource.category
        }
        return if (existingSource != null) {
            existingSource.sourceId
        } else {
            val newSource = SourceEntity(
                writer = source.writer,
                work = source.work,
                category = source.category ?: SourceCategory.OTHER,
            )
            sourceDao.insertSource(newSource)
        }
    }

    suspend fun updateSource(
        source: SourceUiModel,
    ) {
        val existingId = source.existingId ?: return
        val updatedSource = SourceEntity(
            sourceId = existingId,
            writer = source.writer,
            work = source.work,
            category = source.category ?: SourceCategory.OTHER,
        )
        sourceDao.insertSource(updatedSource)
    }

    suspend fun deleteSources(sourceIds: List<Long>): Int {
        return sourceDao.deleteSourcesAndCrossRefs(sourceIds)
    }
}
