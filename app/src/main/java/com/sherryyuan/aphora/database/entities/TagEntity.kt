package com.sherryyuan.aphora.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class TagEntity(
    @PrimaryKey(autoGenerate = true) val tagId: Long = 0,
    val slug: String,
)
