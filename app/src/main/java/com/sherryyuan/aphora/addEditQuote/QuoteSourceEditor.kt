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
import androidx.compose.foundation.text.input.rememberTextFieldState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sherryyuan.aphora.R
import com.sherryyuan.aphora.database.entities.SourceCategory
import com.sherryyuan.aphora.savedQuotes.QuoteUiModel
import com.sherryyuan.aphora.ui.common.VerticalSpacer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteSourceEditor(
    source: QuoteUiModel.Source?,
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
                        contentDescription = "Edit"
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
                    val categoryIconRes = when (category) {
                        SourceCategory.BOOK -> R.drawable.icon_book
                        SourceCategory.MOVIE -> R.drawable.icon_movie
                        SourceCategory.POEM -> R.drawable.icon_book // TODO
                        SourceCategory.TV -> R.drawable.icon_movie
                        SourceCategory.SONG -> R.drawable.icon_music
                        SourceCategory.STORY -> R.drawable.icon_book // TODO
                        SourceCategory.ARTICLE -> R.drawable.icon_book // TODO
                        SourceCategory.OTHER -> R.drawable.icon_book // TODO
                    }
                    Icon(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(categoryIconRes),
                        contentDescription = category.name,
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
    onSaveSource: (QuoteUiModel.Source) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    val authorTextFieldState = rememberTextFieldState(source?.author.orEmpty())
    val workTextFieldState = rememberTextFieldState(source?.work.orEmpty())
    var category: SourceCategory? by remember {
        mutableStateOf(source?.category)
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
        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            state = authorTextFieldState,
            label = { Text(stringResource(R.string.add_edit_quote_source_author)) },
        )
        VerticalSpacer()
        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            state = workTextFieldState,
            label = { Text(stringResource(R.string.add_edit_quote_source_work)) },
        )
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
                    contentDescription = "Dropdown",
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                SourceCategory.entries.forEach { category ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = category.name.lowercase()
                                    .replaceFirstChar { it.uppercase() })
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
