package com.sherryyuan.aphora.addEditQuote

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldLabelPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sherryyuan.aphora.R
import com.sherryyuan.aphora.database.entities.TagEntity
import com.sherryyuan.aphora.savedQuotes.QuoteUiModel
import com.sherryyuan.aphora.ui.common.AphoraCard
import com.sherryyuan.aphora.ui.common.RatingDiamondsRow
import com.sherryyuan.aphora.ui.common.VerticalSpacer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditQuoteContainer(viewModel: AddEditQuoteViewModel) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    val existingQuote = viewState.existingQuote
    val quoteTextFieldState = remember(existingQuote) {
        TextFieldState(existingQuote?.text.orEmpty())
    }
    var rating: Int by remember(existingQuote) {
        mutableIntStateOf(existingQuote?.rating ?: 3)
    }
    var source: QuoteUiModel.Source? by remember(existingQuote) {
        mutableStateOf(existingQuote?.source)
    }
    var selectedTags: List<TagEntity> by remember(existingQuote) {
        mutableStateOf(existingQuote?.tags ?: emptyList())
    }
    val noteTextFieldState = remember(existingQuote) {
        TextFieldState(existingQuote?.userNote.orEmpty())
    }
    val savedEnabled = remember(existingQuote) {
        derivedStateOf {
            quoteTextFieldState.text.isNotBlank()
        }
    }

    Scaffold(
        modifier = Modifier
            .imePadding()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { focusManager.clearFocus() }
                )
            },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(R.drawable.icon_close),
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = stringResource(R.string.label_close),
                        )
                    }
                },
                title = {
                    viewState.topBarTitleRes?.let { Text(stringResource(it)) }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                )
            )
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .consumeWindowInsets(contentPadding)
                .padding(horizontal = 24.dp)
        ) {
            AphoraCard(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    QuoteInputField(quoteTextFieldState)
                    VerticalSpacer()
                    RatingDiamonds(
                        rating = rating,
                        onRatingUpdate = { updatedRating -> rating = updatedRating }
                    )
                    VerticalSpacer(height = 8.dp)
                    QuoteSourceEditor(
                        source = source,
                        allWriters = viewState.allWriters,
                        allSources = viewState.allSources,
                        onSourceUpdated = { source = it },
                    )
                    VerticalSpacer()
                    TagsSelector(
                        selectedTags = selectedTags,
                        allTags = viewState.allTags,
                        onTagSelected = { tag ->
                            if (!selectedTags.contains(tag)) {
                                selectedTags = selectedTags + tag
                            }
                        },
                        onAddNewTagClicked = { label, color ->
                            if (selectedTags.none { it.label.equals(label, ignoreCase = true) }) {
                                val tag = TagEntity(label = label, color = color)
                                viewModel.addNewTag(tag)
                                selectedTags = selectedTags + tag
                            }
                        },
                        onTagUnselected = { tag ->
                            selectedTags = selectedTags - tag
                        },
                    )
                    VerticalSpacer()
                    NotesInputField(noteTextFieldState)
                }
            }
            VerticalSpacer()
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = savedEnabled.value,
                onClick = {
                    viewModel.saveQuote(
                        quoteText = quoteTextFieldState.text.toString(),
                        rating = rating,
                        source = source,
                        tags = selectedTags,
                        noteText = noteTextFieldState.text.toString(),
                    )
                }) {
                Text(stringResource(R.string.add_edit_quote_save_button))
            }
        }
    }
}

@Composable
private fun QuoteInputField(textFieldState: TextFieldState, modifier: Modifier = Modifier) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp),
        state = textFieldState,
        label = { Text(stringResource(R.string.add_edit_quote_aphorism_label)) },
        labelPosition = TextFieldLabelPosition.Attached(alwaysMinimize = true),
        placeholder = { Text(stringResource(R.string.add_edit_quote_aphorism_placeholder)) },
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
    )
}

@Composable
private fun RatingDiamonds(
    modifier: Modifier = Modifier,
    rating: Int,
    onRatingUpdate: (Int) -> Unit
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        RatingDiamondsRow(
            rating = rating,
            diamondSize = 40.dp,
            onRatingClick = {
                focusManager.clearFocus()
                onRatingUpdate(it)
            },
        )
    }
    VerticalSpacer(8.dp)
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.add_edit_quote_rating_low),
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.add_edit_quote_rating_high),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun NotesInputField(textFieldState: TextFieldState, modifier: Modifier = Modifier) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        state = textFieldState,
        label = { Text(stringResource(R.string.quote_notes_section_title)) },
        placeholder = { Text(stringResource(R.string.add_edit_quote_notes_placeholder)) },
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
    )
}
