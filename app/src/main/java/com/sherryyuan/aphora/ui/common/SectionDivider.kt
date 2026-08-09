package com.sherryyuan.aphora.ui.common

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ColumnScope.SectionDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier
            .width(80.dp)
            .align(Alignment.CenterHorizontally),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
    )
}
