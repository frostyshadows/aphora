package com.sherryyuan.aphora.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sherryyuan.aphora.database.entities.SourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SourceDao {

    @Query("SELECT * FROM SourceEntity")
    fun getAllSources(): Flow<List<SourceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSource(source: SourceEntity): Long

    @Transaction
    suspend fun deleteSourcesAndCrossRefs(sourceIds: List<Long>): Int {
        deleteCrossRefsForSources(sourceIds)
        return deleteSources(sourceIds)
    }

    // Call sites outside this DAO should use [deleteSourcesAndCrossRefs]
    @Query("DELETE FROM QuoteSourceCrossRef WHERE sourceId in (:sourceIds)")
    suspend fun deleteCrossRefsForSources(sourceIds: List<Long>)

    // Call sites outside this DAO should use [deleteSourcesAndCrossRefs]
    @Query("DELETE FROM SourceEntity WHERE sourceId in (:sourceIds)")
    suspend fun deleteSources(sourceIds: List<Long>): Int
}
