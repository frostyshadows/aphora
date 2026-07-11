package com.sherryyuan.aphora.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sherryyuan.aphora.database.entities.QuoteDbModel
import com.sherryyuan.aphora.database.entities.QuoteEntity
import com.sherryyuan.aphora.database.entities.Visibility
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface QuotesDao {

    @Query(value = "SELECT * FROM QuoteEntity")
    fun getAll(): Flow<List<QuoteDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: QuoteEntity)

    // TODO: handle updating source and tags
    @Query(value = "UPDATE QuoteEntity SET text= :text, notes = :notes, visibility = :visibility WHERE quoteId = :quoteId")
    suspend fun updateQuote(quoteId: Long, text: String, notes: String, visibility: Visibility)

    @Delete
    suspend fun deleteQuote(quote: QuoteEntity)
}
