package com.sherryyuan.aphora.repository

import androidx.compose.ui.graphics.Color
import com.sherryyuan.aphora.database.TagDao
import com.sherryyuan.aphora.database.entities.DefaultTagColors
import com.sherryyuan.aphora.database.entities.TagEntity
import com.sherryyuan.aphora.savedQuotes.QuoteUiModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TagsRepository @Inject constructor(private val tagDao: TagDao) {

    fun getTags(): Flow<List<TagEntity>> {
        return tagDao.getAll()
    }

    suspend fun saveTag(
        label: String,
        color: Color,
    ): Long {
        val newTag = TagEntity(label = label, color = color)
        return tagDao.insertEntity(newTag)
    }
}
