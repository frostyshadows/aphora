package com.sherryyuan.aphora.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class SourceEntity(
    @PrimaryKey(autoGenerate = true) val sourceId: Long = 0,
    val author: String,
    val work: String?,
    val type: SourceType,
)

enum class SourceType {
    BOOK, MOVIE, POEM, TV, SONG, STORY, ARTICLE, OTHER,
}
