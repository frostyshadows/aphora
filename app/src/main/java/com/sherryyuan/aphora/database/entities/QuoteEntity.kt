package com.sherryyuan.aphora.database.entities

import androidx.annotation.IntRange
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class QuoteEntity(
    @PrimaryKey(autoGenerate = true) val quoteId: Int = 0,
    val text: String,
    val userNote: String?,
    @IntRange(1, 5) val rating: Int,
    val timestampAdded: Long = System.currentTimeMillis(),
    val timestampLastEdited: Long = System.currentTimeMillis(),
)

@Entity(primaryKeys = ["quoteId", "sourceId"])
data class QuoteSourceCrossRef(
    val quoteId: Int,
    val sourceId: Int,
)

@Entity(primaryKeys = ["quoteId", "tagId"])
data class QuoteTagCrossRef(
    val quoteId: Int,
    val tagId: Int,
)
