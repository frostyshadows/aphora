package com.sherryyuan.aphora.savedQuotes

import com.sherryyuan.aphora.database.entities.SourceCategory

data class SourceUiModel(
    // non-null if already saved in db
    val existingId: Long?,
    val writer: String?,
    val work: String?,
    val category: SourceCategory?,
)
