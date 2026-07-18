package com.sherryyuan.aphora.savedQuotes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sherryyuan.aphora.ui.common.AphoraCard
import com.sherryyuan.aphora.ui.theme.Typography

@Composable
fun QuotesList(quotes: List<QuoteUiModel>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(quotes, key = { it.quoteId }) { quote ->
            QuoteRow(quote)
        }
    }
}

@Composable
private fun QuoteRow(model: QuoteUiModel, modifier: Modifier = Modifier) {
    AphoraCard(modifier) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = model.text,
                style = Typography.titleLarge,
            )

            model.source?.let {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(
                    modifier = Modifier
                        .width(80.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(modifier = Modifier.align(Alignment.End), text = it.author.uppercase())
            }
        }
    }
}
