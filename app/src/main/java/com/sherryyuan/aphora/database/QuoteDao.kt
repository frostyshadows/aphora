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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: QuoteEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuoteSourceCrossRef(crossRef: QuoteSourceCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuoteTagCrossRef(crossRef: QuoteTagCrossRef)

    // TODO: handle updating source and tags
    @Query(value = "UPDATE QuoteEntity SET text= :text, userNote = :note WHERE quoteId = :quoteId")
    suspend fun updateQuote(quoteId: Long, text: String, note: String)

    @Query("DELETE FROM QuoteEntity WHERE quoteId = :quoteId")
    suspend fun deleteQuote(quoteId: Long): Int
}
