package com.sherryyuan.aphora.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sherryyuan.aphora.database.entities.QuoteDbModel
import com.sherryyuan.aphora.database.entities.QuoteEntity
import com.sherryyuan.aphora.database.entities.QuoteSourceCrossRef
import com.sherryyuan.aphora.database.entities.QuoteTagCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDao {

    // @Transaction keeps the QuoteEntity query and its @Relation lookups on one consistent
    // snapshot. Without it Room re-runs the parent query while assembling results, and a row
    // committed in between has no entry in the relation maps.
    @Transaction
    @Query(value = "SELECT * FROM QuoteEntity")
    fun getAll(): Flow<List<QuoteDbModel>>

    @Transaction
    @Query(value = "SELECT * FROM QuoteEntity WHERE quoteId = :quoteId")
    suspend fun getQuoteById(quoteId: Long): QuoteDbModel?

    @Transaction
    suspend fun upsertQuoteWithRelations(
        quote: QuoteEntity,
        sourceId: Long?,
        tagIds: List<Long>,
    ): Long {
        val quoteId = insertQuote(quote)
        replaceQuoteRelations(quoteId, sourceId, tagIds)
        return quoteId
    }

    @Transaction
    suspend fun replaceQuoteRelations(quoteId: Long, sourceId: Long?, tagIds: List<Long>) {
        deleteSourceCrossRefsForQuote(quoteId)
        deleteTagCrossRefsForQuote(quoteId)
        sourceId?.let { insertQuoteSourceCrossRef(QuoteSourceCrossRef(quoteId, it)) }
        tagIds.forEach { tagId -> insertQuoteTagCrossRef(QuoteTagCrossRef(quoteId, tagId)) }
    }

    @Transaction
    suspend fun deleteQuoteAndCrossRefs(quoteId: Long): Int {
        deleteSourceCrossRefsForQuote(quoteId)
        deleteTagCrossRefsForQuote(quoteId)
        return deleteQuote(quoteId)
    }

    // Call sites outside this DAO should use [upsertQuoteWithRelations]
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: QuoteEntity): Long

    // Call sites outside this DAO should use [replaceQuoteRelations]
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuoteSourceCrossRef(crossRef: QuoteSourceCrossRef)

    // Call sites outside this DAO should use [replaceQuoteRelations]
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuoteTagCrossRef(crossRef: QuoteTagCrossRef)

    // Call sites outside this DAO should use [replaceQuoteRelations]
    @Query("DELETE FROM QuoteSourceCrossRef WHERE quoteId = :quoteId")
    suspend fun deleteSourceCrossRefsForQuote(quoteId: Long)

    // Call sites outside this DAO should use [replaceQuoteRelations]
    @Query("DELETE FROM QuoteTagCrossRef WHERE quoteId = :quoteId")
    suspend fun deleteTagCrossRefsForQuote(quoteId: Long)

    // Call sites outside this DAO should use [deleteQuoteAndCrossRefs]
    @Query("DELETE FROM QuoteEntity WHERE quoteId = :quoteId")
    suspend fun deleteQuote(quoteId: Long): Int
}
