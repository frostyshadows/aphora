package com.sherryyuan.aphora.repository

import com.sherryyuan.aphora.database.TagDao
import com.sherryyuan.aphora.database.entities.TagEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TagsRepository @Inject constructor(private val tagDao: TagDao) {

    fun getTags(): Flow<List<TagEntity>> {
        return tagDao.getAll()
    }
}
