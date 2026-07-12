package com.sherryyuan.aphora.database.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class QuoteDbModel(
    @Embedded val quote: QuoteEntity,
    @Relation(
        parentColumn = "quoteId",
        entityColumn = "sourceId",
        associateBy = Junction(QuoteSourceCrossRef::class)
    )
    val source: SourceEntity? = null,
    @Relation(
        parentColumn = "quoteId",
        entityColumn = "tagId",
        associateBy = Junction(QuoteTagCrossRef::class)
    )
    val tags: List<TagEntity> = emptyList(),
)
