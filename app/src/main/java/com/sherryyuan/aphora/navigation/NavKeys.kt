package com.sherryyuan.aphora.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object SavedQuotesKey : NavKey

@Serializable
data class AddEditQuoteKey(val quoteId: Long? = null) : NavKey
