package com.sherryyuan.aphora.settings.tagsSettings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sherryyuan.aphora.R
import com.sherryyuan.aphora.ui.theme.DestructiveRed
import com.sherryyuan.aphora.ui.theme.Spacing
import com.sherryyuan.aphora.ui.theme.forTagBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTagsContainer(
    viewModel: SettingsTagsViewModel = hiltViewModel<SettingsTagsViewModel>(),
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()

    val selectedTagLabels = remember {
        mutableStateSetOf<String>()
    }

    LaunchedEffect(viewState.tagsWithCount) {
        selectedTagLabels.clear()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(R.drawable.icon_arrow_left),
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = stringResource(R.string.label_back),
                        )
                    }
                },
                title = {
                    Text(stringResource(R.string.label_tags))
                },
                actions = {
                    AnimatedVisibility(
                        visible = selectedTagLabels.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        IconButton(onClick = { viewModel.deleteClick(selectedTagLabels) }) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(R.drawable.icon_delete),
                                tint = MaterialTheme.colorScheme.onBackground,
                                contentDescription = stringResource(R.string.label_delete),
                            )
                        }
                    }
                    if (viewState.tagsWithCount.isNotEmpty()) {
                        IconButton(onClick = { viewModel.sortClick() }) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(R.drawable.icon_sort),
                                tint = MaterialTheme.colorScheme.onBackground,
                                contentDescription = stringResource(R.string.cd_sort),
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                )
            )
        },
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(contentPadding)
                .consumeWindowInsets(contentPadding),
            contentPadding = PaddingValues(horizontal = Spacing.ScreenMargin),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                items = viewState.tagsWithCount,
                key = { (tag, _) -> tag.tagId },
            ) { (tag, count) ->
                val isSelected = tag.label in selectedTagLabels
                Row(modifier = Modifier.animateItem()) {
                    Checkbox(
                        modifier = Modifier.size(32.dp),
                        checked = isSelected,
                        onCheckedChange = {
                            if (!isSelected) {
                                selectedTagLabels.add(tag.label)
                            } else {
                                selectedTagLabels.remove(tag.label)
                            }
                        }
                    )
                    Text(
                        modifier = Modifier
                            .padding(2.dp)
                            .background(
                                color = tag.color.forTagBackground(),
                                shape = RoundedCornerShape(50),
                            )
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        text = "${tag.label} (${count})",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        when (val state = viewState.modalState) {
            is SettingsTagsViewState.SettingsTagsModalState.SortSheet -> TagsSortBottomSheet(
                selectedSortOrder = state.sortOrder,
                onSortOrderSelected = { viewModel.selectSortOrder(it) },
                onDismiss = { viewModel.dismissModal() },
            )

            is SettingsTagsViewState.SettingsTagsModalState.DeleteDialog -> AlertDialog(
                onDismissRequest = { viewModel.dismissModal() },
                containerColor = MaterialTheme.colorScheme.surface,
                title = { Text(stringResource(R.string.delete_tags_dialog_title)) },
                text = { Text(stringResource(R.string.delete_tags_dialog_message)) },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.deleteSelectedTags(state.selectedTagLabels) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                modifier = Modifier.size(18.dp),
                                painter = painterResource(R.drawable.icon_delete),
                                contentDescription = null,
                                tint = DestructiveRed
                            )
                            Text(
                                text = stringResource(R.string.label_delete),
                                color = DestructiveRed,
                            )
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.dismissModal() }
                    ) {
                        Text(stringResource(R.string.label_cancel))
                    }
                }
            )

            SettingsTagsViewState.SettingsTagsModalState.None -> Unit
        }
    }
}
