package com.sherryyuan.aphora.database

import androidx.compose.ui.graphics.Color
import androidx.room.TypeConverter
import com.sherryyuan.aphora.database.entities.SourceCategory

class Converters {
    @TypeConverter
    fun fromColor(color: Color): Long {
        return color.value.toLong()
    }

    @TypeConverter
    fun toColor(value: Long): Color {
        return Color(value.toULong())
    }
}
