package com.sherryyuan.aphora.repository

import com.sherryyuan.aphora.database.TagDao
import com.sherryyuan.aphora.database.entities.TagEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TagsRepository @Inject constructor(private val tagDao: TagDao) {

    fun getTags(): Flow<List<TagEntity>> {
        return tagDao.getAll()
    }

    // Reference count per tagId. Tags with no references aren't included.
    fun getTagReferenceCounts(): Flow<Map<Long, Int>> {
        return tagDao.getTagReferenceCounts().map { counts ->
            counts.associate { it.tagId to it.referenceCount }
        }
    }

    suspend fun saveTag(tag: TagEntity): Long {
        return tagDao.insertEntity(tag)
    }

    suspend fun deleteTags(labels: List<String>): Int {
        return tagDao.deleteTagsAndCrossRefs(labels)
    }
}
