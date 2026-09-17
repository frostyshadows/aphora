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

    @Query(
        """
        SELECT crossRef.sourceId AS sourceId, COUNT(*) AS referenceCount
        FROM QuoteSourceCrossRef AS crossRef
        INNER JOIN QuoteEntity AS quote ON crossRef.quoteId = quote.quoteId
        GROUP BY crossRef.sourceId
        """
    )
    fun getSourceReferenceCounts(): Flow<List<SourceReferenceCount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSource(source: SourceEntity): Long

    @Transaction
    suspend fun updateSource(source: SourceEntity, duplicateIds: List<Long>) {
        insertSource(source)
        if (duplicateIds.isEmpty()) return
        mergeDuplicateSourceCrossRefs(source.sourceId, duplicateIds)
        deleteSourcesAndCrossRefs(duplicateIds)
    }

    @Query(
        "UPDATE OR IGNORE QuoteSourceCrossRef SET sourceId = :sourceId " +
                "WHERE sourceId in (:duplicateIds)"
    )
    suspend fun mergeDuplicateSourceCrossRefs(sourceId: Long, duplicateIds: List<Long>)

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

data class SourceReferenceCount(
    val sourceId: Long,
    val referenceCount: Int,
)
