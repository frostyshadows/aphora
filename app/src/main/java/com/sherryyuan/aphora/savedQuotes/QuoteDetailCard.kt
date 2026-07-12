package com.sherryyuan.aphora.savedQuotes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sherryyuan.aphora.database.entities.SourceCategory
import com.sherryyuan.aphora.mockData.createQuoteViewModel
import com.sherryyuan.aphora.ui.theme.AphoraTheme
import com.sherryyuan.aphora.ui.theme.Typography

@Composable
fun QuoteDetailCard(model: QuoteUiModel) {
    Card(
        shape = RoundedCornerShape(topEnd = 24.dp, bottomStart = 24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
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
                QuoteSource(modifier = Modifier.align(Alignment.End), model = it)
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun QuoteSource(model: QuoteUiModel.Source, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(end = 24.dp)) {
        Text(
            text = model.author.uppercase(),
            style = Typography.bodyMedium,
        )
        model.work?.let { work ->
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                model.category?.let {
                    val icon = when (it) {
                        SourceCategory.BOOK -> Icons.Outlined.AccountBox
                        SourceCategory.MOVIE -> TODO()
                        SourceCategory.POEM -> TODO()
                        SourceCategory.TV -> TODO()
                        SourceCategory.SONG -> TODO()
                        SourceCategory.STORY -> TODO()
                        SourceCategory.ARTICLE -> TODO()
                        SourceCategory.OTHER -> TODO()
                    }
                    Icon(
                        modifier = Modifier.padding(end = 4.dp),
                        imageVector = icon,
                        contentDescription = null,
                    )
                }
                Text(
                    text = work,
                    style = Typography.bodyMedium,
                )
            }
        }
    }
}

@Preview
@Composable
fun QuoteDetailCardPreview() {
    AphoraTheme {
        Box(modifier = Modifier.fillMaxWidth()) {
            QuoteDetailCard(createQuoteViewModel())
        }
    }
}
