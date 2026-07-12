package com.sherryyuan.aphora.savedQuotes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun SavedQuotesContainer(
    modifier: Modifier = Modifier,
    viewModel: SavedQuotesViewModel = hiltViewModel<SavedQuotesViewModel>(),
) {
    val viewState by viewModel.state.collectAsState()

    LazyColumn(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(viewState.quotes, key = { it.quoteId }) { quote ->
            QuoteRow(quote)
        }
    }
}

@Composable
private fun QuoteRow(model: QuoteUiModel, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(topEnd = 24f, bottomStart = 24f)
    ) {
        Column {
            Text(model.text)
        }
    }
}
