package com.sherryyuan.aphora.addEditQuote

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.sherryyuan.aphora.R
import com.sherryyuan.aphora.database.entities.SourceCategory
import com.sherryyuan.aphora.database.entities.SourceEntity
import com.sherryyuan.aphora.savedQuotes.QuoteUiModel
import com.sherryyuan.aphora.ui.common.VerticalSpacer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteSourceEditor(
    source: QuoteUiModel.Source?,
    allSources: List<SourceEntity>,
    allAuthors: List<String>,
    onSourceUpdated: (QuoteUiModel.Source) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showSourceEditorSheet by remember {
        mutableStateOf(false)
    }
    Card(
        modifier = Modifier
            .padding(24.dp)
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier) {
            if (!showSourceEditorSheet) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showSourceEditorSheet = true },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(stringResource(R.string.add_edit_quote_source_section_title))
                    Icon(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(16.dp),
                        painter = painterResource(R.drawable.icon_pencil),
                        contentDescription = stringResource(R.string.label_edit)
                    )
                }
            } else {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.add_edit_quote_source_section_title)
                )
            }
            source?.author?.let {
                Text(it)
            }
            Row {
                source?.category?.let { category ->
                    Icon(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(category.iconRes),
                        contentDescription = stringResource(category.stringRes),
                    )
                }
                source?.work?.let { Text(it) }
            }
        }
    }

    if (showSourceEditorSheet) {
        ModalBottomSheet(
            dragHandle = null,
            onDismissRequest = { showSourceEditorSheet = false },
        ) {
            SourceEditorSheetContent(
                source = source,
                allAuthors = allAuthors,
                allSources = allSources,
                onSaveSource = {
                    onSourceUpdated(it)
                    showSourceEditorSheet = false
                }
            )
        }
    }
}

@Composable
private fun SourceEditorSheetContent(
    source: QuoteUiModel.Source?,
    allSources: List<SourceEntity>,
    allAuthors: List<String>,
    onSaveSource: (QuoteUiModel.Source) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    val authorTextFieldState = rememberTextFieldState(source?.author.orEmpty())
    val workTextFieldState = rememberTextFieldState(source?.work.orEmpty())
    var category: SourceCategory? by remember {
        mutableStateOf(source?.category)
    }

    val filteredAuthors by remember {
        derivedStateOf {
            val query = authorTextFieldState.text
            if (query.length >= 3) {
                allAuthors.filter { it.contains(query, ignoreCase = true) }
            } else {
                emptyList()
            }
        }
    }

    var showAuthorDropdown by remember { mutableStateOf(false) }

    val filteredWorks by remember {
        derivedStateOf {
            val authorQuery = authorTextFieldState.text.toString()
            val workQuery = workTextFieldState.text
            if (workQuery.length >= 2) {
                allSources
                    .filter { it.author.equals(authorQuery, ignoreCase = true) }
                    .mapNotNull { it.work }
                    .filter { it.contains(workQuery, ignoreCase = true) }
                    .distinct()
            } else {
                emptyList()
            }
        }
    }

    var showWorkDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(filteredAuthors) {
        showAuthorDropdown = filteredAuthors.isNotEmpty()
    }

    LaunchedEffect(filteredWorks) {
        showWorkDropdown = filteredWorks.isNotEmpty()
    }

    Column(
        modifier
            .fillMaxWidth()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { focusManager.clearFocus() }
                )
            }
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.add_edit_quote_source_section_title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
        )
        VerticalSpacer()
        Box {
            OutlinedTextField(
                modifier = modifier.fillMaxWidth(),
                state = authorTextFieldState,
                label = { Text(stringResource(R.string.add_edit_quote_source_author)) },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            )
            DropdownMenu(
                expanded = showAuthorDropdown,
                onDismissRequest = { showAuthorDropdown = false },
                properties = PopupProperties(focusable = false)
            ) {
                filteredAuthors.forEach { author ->
                    DropdownMenuItem(
                        text = { Text(author) },
                        onClick = {
                            authorTextFieldState.setTextAndPlaceCursorAtEnd(author)
                            showAuthorDropdown = false
                        }
                    )
                }
            }
        }
        VerticalSpacer()
        Box {
            OutlinedTextField(
                modifier = modifier.fillMaxWidth(),
                state = workTextFieldState,
                label = { Text(stringResource(R.string.add_edit_quote_source_work)) },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            )
            DropdownMenu(
                expanded = showWorkDropdown,
                onDismissRequest = { showWorkDropdown = false },
                properties = PopupProperties(focusable = false)
            ) {
                filteredWorks.forEach { work ->
                    DropdownMenuItem(
                        text = { Text(work) },
                        onClick = {
                            workTextFieldState.setTextAndPlaceCursorAtEnd(work)
                            showWorkDropdown = false
                        }
                    )
                }
            }
        }
        VerticalSpacer()
        CategoryDropdownMenu(
            selectedCategory = category,
            onCategorySelected = { category = it }
        )
        VerticalSpacer()
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = authorTextFieldState.text.isNotBlank(),
            onClick = {
                onSaveSource(
                    QuoteUiModel.Source(
                        author = authorTextFieldState.text.toString(),
                        work = workTextFieldState.text.toString(),
                        category = category ?: SourceCategory.OTHER
                    )
                )
            }
        ) {
            Text(stringResource(R.string.add_edit_quote_save_button))
        }
        VerticalSpacer()
    }
}

@Composable
private fun CategoryDropdownMenu(
    selectedCategory: SourceCategory?,
    onCategorySelected: (SourceCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var expanded by remember { mutableStateOf(false) }

    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier.padding(end = 4.dp),
            text = stringResource(R.string.add_edit_quote_source_category)
        )
        Box(
            modifier = modifier.clickable {
                focusManager.clearFocus()
                expanded = !expanded
            }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    // match OutlinedTextField's height and border style
                    .heightIn(min = 56.dp)
                    .border(
                        width = if (expanded) 2.dp else 1.dp,
                        color = if (expanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(start = 8.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically

            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = selectedCategory?.name.orEmpty(),
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = stringResource(R.string.cd_dropdown),
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                SourceCategory.entries.forEach { category ->
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(category.stringRes))
                        },
                        onClick = {
                            onCategorySelected(category)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
