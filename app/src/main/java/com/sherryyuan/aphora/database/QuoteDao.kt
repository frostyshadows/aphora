package com.sherryyuan.aphora.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sherryyuan.aphora.database.entities.QuoteDbModel
import com.sherryyuan.aphora.database.entities.QuoteEntity
import com.sherryyuan.aphora.database.entities.QuoteSourceCrossRef
import com.sherryyuan.aphora.database.entities.Visibility
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDao {

    @Query(value = "SELECT * FROM QuoteEntity")
    fun getAll(): Flow<List<QuoteDbModel>>

    @Query(value = "SELECT * FROM QuoteEntity WHERE quoteId= :id")
    suspend fun getQuoteById(id: Long): QuoteDbModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: QuoteEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuoteSourceCrossRef(crossRef: QuoteSourceCrossRef)

    // TODO: handle updating source and tags
    @Query(value = "UPDATE QuoteEntity SET text= :text, userNote = :note, visibility = :visibility WHERE quoteId = :quoteId")
    suspend fun updateQuote(quoteId: Long, text: String, note: String, visibility: Visibility)

    @Delete
    suspend fun deleteQuote(quote: QuoteEntity)
}
