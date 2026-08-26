package com.sherryyuan.aphora.database.entities

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.room.Entity
import androidx.room.PrimaryKey

import com.sherryyuan.aphora.R

@Entity
data class SourceEntity(
    @PrimaryKey(autoGenerate = true) val sourceId: Long = 0,
    val author: String?,
    val work: String?,
    val category: SourceCategory,
)

enum class SourceCategory(
    @DrawableRes val iconRes: Int,
    @StringRes val stringRes: Int
) {
    BOOK(R.drawable.icon_book, R.string.category_book),
    MOVIE(R.drawable.icon_movie, R.string.category_movie),
    TV(R.drawable.icon_tv, R.string.category_tv),
    SONG(R.drawable.icon_music, R.string.category_song),
    POEM(R.drawable.icon_book, R.string.category_poem), // TODO
    SHORT_STORY(R.drawable.icon_book, R.string.category_story), // TODO
    ARTICLE(R.drawable.icon_article, R.string.category_article),
    OTHER(R.drawable.icon_book, R.string.category_other), // TODO
}
