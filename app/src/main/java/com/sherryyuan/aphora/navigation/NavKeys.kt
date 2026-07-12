package com.sherryyuan.aphora.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object SavedQuotes : NavKey

@Serializable
object AddQuoteNavKey : NavKey
