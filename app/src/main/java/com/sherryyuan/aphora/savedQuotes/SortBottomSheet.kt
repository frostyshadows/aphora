package com.sherryyuan.aphora.savedQuotes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sherryyuan.aphora.R
import com.sherryyuan.aphora.database.entities.SortOption
import com.sherryyuan.aphora.ui.common.VerticalSpacer
import com.sherryyuan.aphora.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    selectedSortOption: SortOption,
    onOptionSelected: (SortOption) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var currentSelectedOption by remember(selectedSortOption) {
        mutableStateOf(selectedSortOption)
    }

    ModalBottomSheet(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        onDismissRequest = onDismiss,
    ) {
        Column {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.sort_by_sheet_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )
            VerticalSpacer()
            SortOption.entries.forEach { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = entry == currentSelectedOption,
                            onClick = { currentSelectedOption = entry },
                            role = Role.RadioButton,
                        )
                        .padding(vertical = 10.dp, horizontal = Spacing.ScreenMargin),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(modifier = Modifier.weight(1f), text = stringResource(entry.stringRes))
                    RadioButton(
                        selected = entry == currentSelectedOption,
                        onClick = null,
                    )
                }
            }
            VerticalSpacer()
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.ScreenMargin),
                onClick = { onOptionSelected(currentSelectedOption) },
            ) { Text(stringResource(R.string.label_confirm)) }
        }
    }
}
