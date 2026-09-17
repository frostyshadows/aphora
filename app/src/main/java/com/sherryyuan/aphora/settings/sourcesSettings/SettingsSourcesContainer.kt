package com.sherryyuan.aphora.settings.sourcesSettings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.sherryyuan.aphora.settings.sourcesSettings.SettingsSourcesViewState.SettingsSourcesModalState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sherryyuan.aphora.R
import com.sherryyuan.aphora.addEditQuote.SourceEditorMode
import com.sherryyuan.aphora.addEditQuote.SourceEditorSheetContent
import com.sherryyuan.aphora.ui.common.AphoraBottomSheet
import com.sherryyuan.aphora.ui.theme.DestructiveRed
import com.sherryyuan.aphora.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSourcesContainer(
    viewModel: SettingsSourcesViewModel = hiltViewModel<SettingsSourcesViewModel>(),
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()

    val selectedSourceIds = remember {
        mutableStateSetOf<Long>()
    }

    LaunchedEffect(viewState.sourcesWithCount) {
        selectedSourceIds.clear()
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
                    Text(stringResource(R.string.label_sources))
                },
                actions = {
                    AnimatedVisibility(
                        visible = selectedSourceIds.count() == 1,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        IconButton(
                            onClick = {
                                selectedSourceIds.singleOrNull()?.let { viewModel.editClick(it) }
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(R.drawable.icon_pencil),
                                tint = MaterialTheme.colorScheme.onBackground,
                                contentDescription = stringResource(R.string.label_edit),
                            )
                        }
                    }
                    AnimatedVisibility(
                        visible = selectedSourceIds.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        IconButton(onClick = { viewModel.deleteClick(selectedSourceIds) }) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(R.drawable.icon_delete),
                                tint = MaterialTheme.colorScheme.onBackground,
                                contentDescription = stringResource(R.string.label_delete),
                            )
                        }
                    }
                    if (viewState.sourcesWithCount.isNotEmpty()) {
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
                items = viewState.sourcesWithCount,
                key = { (source, _) -> source.toString() },
            ) { (source, count) ->
                if (source.existingId == null) return@items
                val isSelected = source.existingId in selectedSourceIds
                Row(modifier = Modifier.animateItem()) {
                    Checkbox(
                        modifier = Modifier.size(32.dp),
                        checked = isSelected,
                        onCheckedChange = {
                            if (!isSelected) {
                                selectedSourceIds.add(source.existingId)
                            } else {
                                selectedSourceIds.remove(source.existingId)
                            }
                        }
                    )
                    val displayedSource = buildAnnotatedString {
                        source.work?.let {
                            withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                                append(it)
                            }
                        }
                        if (!source.work.isNullOrBlank() && !source.writer.isNullOrBlank()) {
                            append(" - ")
                        }
                        source.writer?.let { append(it) }
                        append(" ($count)")
                    }
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp),
                        text = displayedSource,
                    )
                }
            }
        }

        when (val state = viewState.modalState) {
            is SettingsSourcesModalState.SortSheet -> SourcesSortBottomSheet(
                selectedSortOrder = state.sortOrder,
                onSortOrderSelected = { viewModel.selectSortOrder(it) },
                onDismiss = { viewModel.dismissModal() },
            )

            is SettingsSourcesModalState.DeleteDialog -> AlertDialog(
                onDismissRequest = { viewModel.dismissModal() },
                containerColor = MaterialTheme.colorScheme.surface,
                title = { Text(stringResource(R.string.delete_sources_dialog_title)) },
                text = { Text(stringResource(R.string.delete_sources_dialog_message)) },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.deleteSelectedSources(state.selectedSourceIds) }
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

            is SettingsSourcesModalState.EditSource -> AphoraBottomSheet(
                onDismissRequest = { viewModel.dismissModal() },
            ) {
                SourceEditorSheetContent(
                    source = state.source,
                    allSources = state.allSources,
                    mode = SourceEditorMode.EDIT_EXISTING,
                    onSaveSource = {
                        viewModel.saveSource(it)
                        selectedSourceIds.clear()
                    }
                )
            }

            SettingsSourcesModalState.None -> Unit
        }
    }
}
