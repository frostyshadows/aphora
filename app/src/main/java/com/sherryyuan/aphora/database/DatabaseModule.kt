package com.sherryyuan.aphora.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "aphora_database"
        ).build()
    }

    @Singleton
    @Provides
    fun provideQuoteDao(database: AppDatabase): QuoteDao {
        return database.quoteDao()
    }

    @Singleton
    @Provides
    fun provideSourceDao(database: AppDatabase): SourceDao {
        return database.sourceDao()
    }
}
