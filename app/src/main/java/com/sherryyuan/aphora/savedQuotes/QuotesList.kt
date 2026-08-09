package com.sherryyuan.aphora.savedQuotes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sherryyuan.aphora.R
import com.sherryyuan.aphora.ui.common.AphoraCard
import com.sherryyuan.aphora.ui.common.SectionDivider
import com.sherryyuan.aphora.ui.common.VerticalSpacer
import com.sherryyuan.aphora.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesList(
    quotes: List<QuoteUiModel>,
    onRandomQuoteClick: () -> Unit,
    onQuoteRowClick: (Int) -> Unit,
    onAddQuoteClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onRandomQuoteClick) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(R.drawable.icon_shuffle),
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = stringResource(R.string.cd_shuffle)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.size(56.dp),
                onClick = onAddQuoteClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.icon_pencil),
                    contentDescription = stringResource(R.string.cd_add_quote)
                )
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            itemsIndexed(
                items = quotes,
                key = { _, quote -> quote.quoteId },
            ) { index, quote ->
                QuoteRow(
                    modifier = Modifier.clickable { onQuoteRowClick(index) },
                    model = quote,
                )
            }
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
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
            )

            model.source?.let {
                VerticalSpacer()
                SectionDivider()
                VerticalSpacer(12.dp)
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = it.author.uppercase(),
                    textAlign = TextAlign.End,
                )
            }
        }
    }
}
