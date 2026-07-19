package com.sherryyuan.aphora.database.entities

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sherryyuan.aphora.ui.theme.TagPastelBlue
import com.sherryyuan.aphora.ui.theme.TagPastelGreen
import com.sherryyuan.aphora.ui.theme.TagPastelOrange
import com.sherryyuan.aphora.ui.theme.TagPastelPink
import com.sherryyuan.aphora.ui.theme.TagPastelPurple
import com.sherryyuan.aphora.ui.theme.TagPastelRed
import com.sherryyuan.aphora.ui.theme.TagPastelYellow

@Entity
data class TagEntity(
    @PrimaryKey(autoGenerate = true) val tagId: Int = 0,
    val label: String,
    val color: Color = DefaultTagColors[tagId % DefaultTagColors.size],
)

val DefaultTagColors = listOf(
    TagPastelRed,
    TagPastelOrange,
    TagPastelYellow,
    TagPastelGreen,
    TagPastelBlue,
    TagPastelPurple,
    TagPastelPink
)
