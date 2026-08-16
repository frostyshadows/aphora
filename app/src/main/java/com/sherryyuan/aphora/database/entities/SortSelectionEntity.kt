package com.sherryyuan.aphora.database.entities

import androidx.annotation.StringRes
import androidx.room.Entity
import androidx.room.PrimaryKey

import com.sherryyuan.aphora.R

@Entity
data class SortSelectionEntity(
    @PrimaryKey val id: Int = 0, // store a single row
    val sortOption: SortOption,
)

enum class SortOption(@StringRes val stringRes: Int) {
    MOST_LIKED(R.string.sort_option_most_liked),
    LEAST_LIKED(R.string.sort_option_least_liked),
    EARLIEST_ADDED(R.string.sort_option_earliest_added),
    MOST_RECENT_ADDED(R.string.sort_option_most_recent_added),
    MOST_RECENT_UPDATED(R.string.sort_option_most_recent_updated),
}
