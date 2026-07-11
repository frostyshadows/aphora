package com.sherryyuan.aphora.savedQuotes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sherryyuan.aphora.mockData.createQuoteViewModel

@Composable
fun QuoteDetailCard(model: QuoteUiModel) {
    Card(shape = RoundedCornerShape(topEnd = 24f, bottomStart = 24f)) {
        Column {
            Text(model.text)

            model.note?.let {
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                Text(it)
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Preview
@Composable
fun QuoteDetailCardPreview() {
    Box(modifier = Modifier.fillMaxWidth()) {
        QuoteDetailCard(createQuoteViewModel())
    }
}
