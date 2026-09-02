package com.sherryyuan.aphora.savedQuotes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sherryyuan.aphora.R
import com.sherryyuan.aphora.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteDetailPager(
    quotes: List<QuoteUiModel>,
    currentIndex: Int,
    onBackClick: () -> Unit,
    onGoToPreviousClick: () -> Unit,
    onGoToNextClick: () -> Unit,
    onSwipeToQuote: (Long) -> Unit,
    onRandomQuoteClick: () -> Unit,
    onEditQuoteClick: () -> Unit,
    onDeleteQuoteClick: () -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = currentIndex, pageCount = { quotes.size })
    LaunchedEffect(currentIndex) {
        if (pagerState.currentPage != currentIndex) {
            pagerState.animateScrollToPage(currentIndex)
        }
    }
    LaunchedEffect(pagerState.settledPage) {
        onSwipeToQuote(quotes[pagerState.settledPage].quoteId)
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
                actions = {
                    IconButton(onClick = onRandomQuoteClick) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(R.drawable.icon_shuffle),
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = stringResource(R.string.cd_random)
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
                previousVisible = currentIndex > 0,
                onGoToPreviousClick = onGoToPreviousClick,
                nextVisible = currentIndex < quotes.lastIndex,
                onGoToNextClick = onGoToNextClick,
            )
        }
    ) { contentPadding ->
        HorizontalPager(
            modifier = Modifier
                .padding(contentPadding)
                .consumeWindowInsets(contentPadding),
            state = pagerState,
            key = { index -> quotes[index].quoteId },
            contentPadding = PaddingValues(horizontal = Spacing.ScreenMargin),
            pageSpacing = 40.dp,
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 20.dp),
                contentAlignment = TopCenter,
            ) {
                QuoteDetailCard(
                    model = quotes[page],
                    onEditQuoteClick = onEditQuoteClick,
                    onDeleteQuoteClick = {
                        onDeleteQuoteClick()
                    },
                    onShareQuoteClick = { /** TODO **/ },
                )
            }
        }
    }
}

@Composable
private fun QuoteDetailBottomBar(
    previousVisible: Boolean,
    nextVisible: Boolean,
    onGoToPreviousClick: () -> Unit,
    onGoToNextClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
    ) {
        AnimatedVisibility(visible = previousVisible, enter = fadeIn(), exit = fadeOut()) {
            IconButton(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape,
                    ),
                onClick = onGoToPreviousClick,
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.icon_caret_left),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = stringResource(R.string.cd_previous),
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        AnimatedVisibility(visible = nextVisible, enter = fadeIn(), exit = fadeOut()) {
            IconButton(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape,
                    ),
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
}
