package com.sherryyuan.aphora.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sherryyuan.aphora.savedQuotes.QuoteUiModel
import com.sherryyuan.aphora.ui.theme.Typography

@Composable
fun QuoteSource(model: QuoteUiModel.Source, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        model.writer?.let { writer ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                QuoteSourceIcon(model)
                Text(
                    text = writer.uppercase(),
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

        }
        if (!model.writer.isNullOrBlank() && !model.work.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
        }
        model.work?.let { work ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (model.writer.isNullOrBlank()) {
                    QuoteSourceIcon(model)
                }
                Text(
                    text = work,
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
    }
}

@Composable
private fun QuoteSourceIcon(model: QuoteUiModel.Source) {
    model.category?.let { category ->
        Icon(
            modifier = Modifier
                .padding(end = 4.dp)
                .size(16.dp),
            painter = painterResource(category.iconRes),
            contentDescription = stringResource(category.stringRes),
        )
    }
}
