package com.sherryyuan.aphora.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sherryyuan.aphora.database.entities.QuoteEntity
import com.sherryyuan.aphora.database.entities.QuoteSourceCrossRef
import com.sherryyuan.aphora.database.entities.QuoteTagCrossRef
import com.sherryyuan.aphora.database.entities.SourceEntity
import com.sherryyuan.aphora.database.entities.TagEntity

@Database(
    entities = [
        QuoteEntity::class,
        SourceEntity::class,
        TagEntity::class,
        QuoteSourceCrossRef::class,
        QuoteTagCrossRef::class,
    ],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun quoteDao(): QuoteDao

    abstract fun sourceDao(): SourceDao
}
