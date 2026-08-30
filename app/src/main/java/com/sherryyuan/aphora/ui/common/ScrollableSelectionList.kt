package com.sherryyuan.aphora.ui.common

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun <T> ScrollableSelectionList(
    items: List<T>,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T) -> Unit,
) {
    val scrollState = rememberScrollState()
    Box(modifier = modifier.heightIn(max = 200.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 8.dp),
        ) {
            items.forEach { item -> itemContent(item) }
        }
        if (scrollState.maxValue > 0 && scrollState.isScrollInProgress) {
            ScrollIndicator(
                scrollState = scrollState,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight(),
            )
        }
    }
}

@Composable
private fun ScrollIndicator(scrollState: ScrollState, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier) {
        val trackHeightPx = with(LocalDensity.current) {
            maxHeight.toPx()
        }
        val scrollableContentHeightPx = trackHeightPx + scrollState.maxValue
        val visibleContentFraction = (trackHeightPx / scrollableContentHeightPx).coerceIn(0.1f, 1f)
        val indicatorHeight = maxHeight * visibleContentFraction
        val indicatorOffsetRange = maxHeight - indicatorHeight
        Box(
            modifier = Modifier
                .graphicsLayer {
                    val indicatorOffsetFraction = if (scrollState.maxValue > 0) {
                        scrollState.value / scrollState.maxValue.toFloat()
                    } else {
                        0f
                    }
                    translationY = indicatorOffsetRange.toPx() * indicatorOffsetFraction
                }
                .width(4.dp)
                .height(indicatorHeight)
                .background(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(2.dp),
                )
        )
    }
}
