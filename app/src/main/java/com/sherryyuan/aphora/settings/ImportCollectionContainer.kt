package com.sherryyuan.aphora.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sherryyuan.aphora.R
import com.sherryyuan.aphora.ui.common.VerticalSpacer
import com.sherryyuan.aphora.ui.theme.AphoraTheme
import com.sherryyuan.aphora.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportCollectionContainer(
    viewModel: ImportCollectionViewModel,
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()

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
                title = { Text(viewState.title) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                )
            )
        },
    ) { contentPadding ->
        when (val state = viewState) {
            is ImportCollectionViewState.Loaded ->
                LoadedCollection(
                    modifier = Modifier
                        .padding(contentPadding)
                        .consumeWindowInsets(contentPadding)
                        .fillMaxSize(),
                    viewState = state,
                    onImportClick = { viewModel.importQuotes(it) },
                    onImportSuccessDoneClick = { viewModel.onSuccessDoneClicked() },
                )

            is ImportCollectionViewState.Error -> Box(contentAlignment = Alignment.Center) {
                Text(text = stringResource(R.string.collection_error))
            }
        }
    }
}

@Composable
private fun LoadedCollection(
    viewState: ImportCollectionViewState.Loaded,
    onImportClick: (Set<Int>) -> Unit,
    onImportSuccessDoneClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedQuoteIndices = remember {
        mutableStateSetOf<Int>()
    }
    Box(modifier) {
        Column {
            Text(
                modifier = Modifier.padding(horizontal = Spacing.ScreenMargin),
                text = viewState.description,
            )
            VerticalSpacer()
            Row(
                modifier = Modifier.padding(horizontal = Spacing.ScreenMargin),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val allPreviewsChecked =
                    selectedQuoteIndices.size == viewState.previews.size && selectedQuoteIndices.isNotEmpty()
                Checkbox(
                    modifier = Modifier.size(32.dp),
                    checked = allPreviewsChecked,
                    onCheckedChange = {
                        if (!allPreviewsChecked) {
                            selectedQuoteIndices.addAll(viewState.previews.indices)
                        } else {
                            selectedQuoteIndices.clear()
                        }
                    }
                )
                Text(stringResource(R.string.label_add_all))
            }
            VerticalSpacer(8.dp)
            SettingsDivider()
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 64.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .verticalScroll(rememberScrollState()),
            ) {
                viewState.previews.forEachIndexed { index, preview ->
                    SnippetPreviewRow(
                        model = preview,
                        checked = index in selectedQuoteIndices,
                        onCheckedChange = { checked ->
                            if (checked) {
                                selectedQuoteIndices.add(index)
                            } else {
                                selectedQuoteIndices.remove(index)
                            }
                        }
                    )
                    SettingsDivider()
                }
            }
        }
        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .padding(
                    top = 10.dp,
                    start = Spacing.ScreenMargin,
                    end = Spacing.ScreenMargin,
                ),
            enabled = selectedQuoteIndices.isNotEmpty(),
            onClick = {
                onImportClick(selectedQuoteIndices)
            }
        ) {
            Text(stringResource(R.string.collection_import_button))
        }
    }
    viewState.importSuccessDialog?.let {
        AlertDialog(
            onDismissRequest = onImportSuccessDoneClick,
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(stringResource(R.string.collection_import_success_dialog_title))
            },
            text = {
                Text(
                    pluralStringResource(
                        R.plurals.collection_import_success_dialog_message,
                        it.snippetCount,
                        it.snippetCount,
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = onImportSuccessDoneClick) {
                    Text(stringResource(R.string.label_done))
                }
            },
        )
    }
}

@Composable
private fun SnippetPreviewRow(
    model: ImportCollectionViewState.SnippetPreview,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val textColor = MaterialTheme.colorScheme.onSurface.copy(
        alpha = if (model.alreadySelected) 0.5f else 1f
    )
    Row(modifier = modifier.padding(vertical = 8.dp, horizontal = Spacing.ScreenMargin)) {
        Checkbox(
            modifier = Modifier.size(32.dp),
            checked = checked || model.alreadySelected,
            onCheckedChange = onCheckedChange,
            enabled = !model.alreadySelected,
        )
        Column {
            Text(
                text = model.text,
                color = textColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            VerticalSpacer(4.dp)
            Text(
                modifier = Modifier.align(Alignment.End),
                text = "— ${model.writer}",
                color = textColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview
@Composable
fun LoadedCollectionPreview() {
    AphoraTheme {
        LoadedCollection(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            viewState = ImportCollectionViewState.Loaded(
                title = "Beautiful prose",
                description = "A collection of beautifully written passages from classic novels.",
                previews = listOf(
                    ImportCollectionViewState.SnippetPreview(
                        text = "It was a bright cold day in April, and the clocks were striking thirteen.",
                        writer = "George Orwell",
                        alreadySelected = false,
                    ),
                    ImportCollectionViewState.SnippetPreview(
                        text = "So we beat on, boats against the current, borne back ceaselessly into the past.",
                        writer = "F. Scott Fitzgerald",
                        alreadySelected = true,
                    ),
                    ImportCollectionViewState.SnippetPreview(
                        text = "All happy families are alike; each unhappy family is unhappy in its own way.",
                        writer = "Leo Tolstoy",
                        alreadySelected = false,
                    ),
                ),
                importSuccessDialog = null,
            ),
            onImportClick = {},
            onImportSuccessDoneClick = {},
        )
    }
}
