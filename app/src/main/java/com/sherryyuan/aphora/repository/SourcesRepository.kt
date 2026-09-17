package com.sherryyuan.aphora.repository

import com.sherryyuan.aphora.database.SourceDao
import com.sherryyuan.aphora.database.entities.SourceCategory
import com.sherryyuan.aphora.database.entities.SourceEntity
import com.sherryyuan.aphora.savedQuotes.SourceUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SourcesRepository @Inject constructor(private val sourceDao: SourceDao) {

    fun getAllSources(): Flow<List<SourceEntity>> {
        return sourceDao.getAllSources()
    }

    // Reference count per sourceId. Sources with no references aren't included.
    fun getSourceReferenceCounts(): Flow<Map<Long, Int>> {
        return sourceDao.getSourceReferenceCounts().map { counts ->
            counts.associate { it.sourceId to it.referenceCount }
        }
    }

    // Save a new source, or no-op if it's a duplicate of an existing one
    suspend fun saveSource(
        source: SourceUiModel,
    ): Long {
        val newSource = source.toEntity()
        val existingSource = sourceDao.getAllSources().first()
            .firstOrNull { it.isDuplicateOf(newSource) }
        return existingSource?.sourceId ?: sourceDao.insertSource(newSource)
    }

    suspend fun updateSource(
        source: SourceUiModel,
    ) {
        val existingId = source.existingId ?: return
        val updatedSource = source.toEntity(sourceId = existingId)
        // An edit can make this source identical to others (eg. fixing typo in a writer's name).
        // Merge any new duplicates.
        val duplicateIds = sourceDao.getAllSources().first()
            .filter { it.sourceId != existingId && it.isDuplicateOf(updatedSource) }
            .map { it.sourceId }
        sourceDao.updateSource(updatedSource, duplicateIds)
    }

    suspend fun deleteSources(sourceIds: List<Long>): Int {
        return sourceDao.deleteSourcesAndCrossRefs(sourceIds)
    }

    // sourceId 0 lets Room autogenerate one for a source that is not saved yet.
    private fun SourceUiModel.toEntity(sourceId: Long = 0): SourceEntity {
        return SourceEntity(
            sourceId = sourceId,
            writer = writer?.trim()?.takeIf { it.isNotEmpty() },
            work = work?.trim()?.takeIf { it.isNotEmpty() },
            category = category ?: SourceCategory.OTHER,
        )
    }

    private fun SourceEntity.isDuplicateOf(other: SourceEntity): Boolean {
        return writer.equals(other.writer, ignoreCase = true) &&
                work.equals(other.work, ignoreCase = true) &&
                category == other.category
    }
}
