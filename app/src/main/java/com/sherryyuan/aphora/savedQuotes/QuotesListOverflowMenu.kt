package com.sherryyuan.aphora.savedQuotes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sherryyuan.aphora.R

@Composable
fun QuotesListOverflowMenu(
    showSortOption: Boolean,
    onSortClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(R.drawable.icon_overflow),
                tint = MaterialTheme.colorScheme.onBackground,
                contentDescription = stringResource(R.string.cd_more_options)
            )
        }
        DropdownMenu(
            expanded = expanded,
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = { expanded = false },
        ) {
            if (showSortOption) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.overflow_menu_label_sort)) },
                    onClick = onSortClick,
                )
            }
            DropdownMenuItem(
                text = { Text(stringResource(R.string.label_settings)) },
                onClick = onSettingsClick,
            )
        }
    }
}
