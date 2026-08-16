package com.sherryyuan.aphora.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sherryyuan.aphora.database.entities.SortSelectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SortSelectionDao {
    @Query(value = "SELECT * FROM SortSelectionEntity LIMIT 1")
    fun getSelection(): Flow<SortSelectionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSelection(selection: SortSelectionEntity): Long
}
