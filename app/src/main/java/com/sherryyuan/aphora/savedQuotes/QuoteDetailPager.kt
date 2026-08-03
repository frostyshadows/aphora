package com.sherryyuan.aphora.savedQuotes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sherryyuan.aphora.R
import kotlin.compareTo
import kotlin.text.compareTo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteDetailPager(
    quotes: List<QuoteUiModel>,
    currentIndex: Int,
    onBackClick: () -> Unit,
    onGoToPreviousClick: () -> Unit,
    onGoToNextClick: () -> Unit,
    onSwipeToIndex: (Int) -> Unit,
    onRandomQuoteClick: () -> Unit,
    onEditQuoteClick: () -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = currentIndex, pageCount = { quotes.size })
    LaunchedEffect(currentIndex) {
        if (pagerState.currentPage != currentIndex) {
            pagerState.animateScrollToPage(currentIndex)
        }
    }
    LaunchedEffect(pagerState) {
        // Report swipes back to view model, once settled
        snapshotFlow { pagerState.currentPage }
            .collect { page ->
                if (page != currentIndex) {
                    onSwipeToIndex(page)
                }
            }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(R.drawable.icon_arrow_left),
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = stringResource(R.string.label_back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                )
            )
        },
        bottomBar = {
            QuoteDetailBottomBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp),
                previousEnabled = currentIndex > 0,
                onGoToPreviousClick = onGoToPreviousClick,
                nextEnabled = currentIndex < quotes.lastIndex,
                onGoToNextClick = onGoToNextClick,
            )
        }
    ) { contentPadding ->
        HorizontalPager(
            modifier = Modifier
                .padding(contentPadding)
                .consumeWindowInsets(contentPadding)
                .padding(horizontal = 24.dp),
            state = pagerState,
        ) { page ->
            QuoteDetailCard(quotes[page])
        }
    }
}

@Composable
private fun QuoteDetailBottomBar(
    previousEnabled: Boolean,
    nextEnabled: Boolean,
    onGoToPreviousClick: () -> Unit,
    onGoToNextClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(
                        alpha = if (previousEnabled) 1f else 0.5f
                    ),
                    shape = CircleShape,
                ),
            enabled = previousEnabled,
            onClick = onGoToPreviousClick,
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.icon_caret_left),
                tint = MaterialTheme.colorScheme.onPrimary,
                contentDescription = stringResource(R.string.cd_previous),
            )
        }
        IconButton(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(
                        alpha = if (nextEnabled) 1f else 0.5f
                    ),
                    shape = CircleShape,
                ),
            enabled = nextEnabled,
            onClick = onGoToNextClick,
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.icon_caret_right),
                tint = MaterialTheme.colorScheme.onPrimary,
                contentDescription = stringResource(R.string.cd_next),
            )
        }
    }
}
