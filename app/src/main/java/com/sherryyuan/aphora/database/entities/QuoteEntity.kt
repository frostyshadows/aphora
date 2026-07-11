package com.sherryyuan.aphora.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class QuoteEntity(
    @PrimaryKey(autoGenerate = true) val quoteId: Long = 0,
    val text: String,
    val notes: String,
    val visibility: Visibility,
    val timestampAdded: Long = System.currentTimeMillis(),
    val timestampLastEdited: Long = System.currentTimeMillis(),
)

enum class Visibility {
    PRIVATE, FRIENDS, PUBLIC,
}

@Entity(primaryKeys = ["quoteId", "sourceId"])
data class QuoteSourceCrossRef(
    val quoteId: Long,
    val sourceId: Long,
)

@Entity(primaryKeys = ["quoteId", "tagId"])
data class QuoteTagCrossRef(
    val quoteId: Long,
    val sourceId: Long,
)
