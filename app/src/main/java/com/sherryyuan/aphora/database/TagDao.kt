package com.sherryyuan.aphora.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sherryyuan.aphora.database.entities.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Query(value = "SELECT * FROM TagEntity")
    fun getAll(): Flow<List<TagEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntity(source: TagEntity): Long

    @Query("SELECT tagId FROM TagEntity WHERE label in (:labels)")
    suspend fun getTagIds(labels: List<String>): List<Long>

    @Transaction
    suspend fun deleteTagsAndCrossRefs(labels: List<String>): Int {
        deleteCrossRefsForTags(getTagIds(labels))
        return deleteTags(labels)
    }

    // Call sites outside this DAO should use [deleteTagsAndCrossRefs]
    @Query("DELETE FROM QuoteTagCrossRef WHERE tagId in (:tagIds)")
    suspend fun deleteCrossRefsForTags(tagIds: List<Long>)

    // Call sites outside this DAO should use [deleteTagsAndCrossRefs]
    @Query("DELETE FROM TagEntity WHERE label in (:labels)")
    suspend fun deleteTags(labels: List<String>): Int
}
