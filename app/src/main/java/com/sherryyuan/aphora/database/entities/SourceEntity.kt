package com.sherryyuan.aphora.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SourceEntity(
    @PrimaryKey(autoGenerate = true) val sourceId: Long = 0,
    val author: String,
    val work: String?,
    val category: SourceCategory,
)

enum class SourceCategory {
    BOOK, MOVIE, POEM, TV, SONG, STORY, ARTICLE, OTHER,
}
