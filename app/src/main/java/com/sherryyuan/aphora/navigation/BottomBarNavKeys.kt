package com.sherryyuan.aphora.navigation

import com.sherryyuan.aphora.R
import androidx.compose.runtime.saveable.Saver
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface BottomBarNavKey : NavKey {
    val iconRes: Int
    val label: String

    @Serializable
    data object SavedQuotes : BottomBarNavKey {
        override val iconRes: Int = R.drawable.ic_favorite
        override val label: String = "Saved"
    }

    @Serializable
    data object Explore : BottomBarNavKey {
        override val iconRes: Int = R.drawable.ic_home
        override val label: String = "Explore"
    }

    companion object {
        val entries = listOf(SavedQuotes, Explore)

        val stateSaver = Saver<BottomBarNavKey, String>(
            save = { it::class.qualifiedName },
            restore = { qualifiedClass ->
                entries.firstOrNull { it::class.qualifiedName == qualifiedClass }
                    ?: SavedQuotes
            }
        )
    }
}
