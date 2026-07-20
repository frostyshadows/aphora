package com.sherryyuan.aphora.addEditQuote

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.sherryyuan.aphora.R
import com.sherryyuan.aphora.database.entities.DefaultTagColors
import com.sherryyuan.aphora.database.entities.TagEntity

@Composable
fun TagsSelector(
    selectedTags: List<TagEntity>,
    allTags: List<TagEntity>,
    onTagSelected: (TagEntity) -> Unit,
    onAddNewTagClicked: (String) -> Unit,
    onTagUnselected: (TagEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val randomNewTagColor = remember {
        DefaultTagColors[DefaultTagColors.indices.random()]
    }
    val focusRequester = remember {
        FocusRequester()
    }

    var isFocused by remember {
        mutableStateOf(false)
    }

    val inputTextFieldState = rememberTextFieldState("")
    val inputText = inputTextFieldState.text.trim().toString()

    var showDropdown by remember {
        mutableStateOf(false)
    }

    val filteredTags by remember(inputText, allTags, selectedTags) {
        derivedStateOf {
            allTags
                .filter { tag ->
                    tag.label.contains(inputText, ignoreCase = true) &&
                            selectedTags.none { it.tagId == tag.tagId }
                }
        }
    }

    LaunchedEffect(inputText, filteredTags) {
        if (isFocused && (inputText.isNotBlank() || filteredTags.isNotEmpty())) {
            showDropdown = true
        }
        if (filteredTags.isEmpty() && inputText.isBlank()) {
            showDropdown = false
        }
    }

    Column {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.add_edit_quote_tags_section_title)
        )

        FlowRow(
            modifier = modifier.border(width = 1.dp, color = MaterialTheme.colorScheme.onSurface),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            selectedTags.forEach { item ->
                key(item.tagId) {
                    SelectedTagChip(item, onRemoveClick = { onTagUnselected(item) })
                }
            }

            Box(
                modifier = Modifier
                    .height(54.dp)
                    .widthIn(min = 80.dp)
                    .weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    state = inputTextFieldState,
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            isFocused = it.isFocused
                            showDropdown =
                                it.isFocused && (inputText.isNotBlank() || filteredTags.isNotEmpty())
                        },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                )

                DropdownMenu(
                    expanded = showDropdown,
                    properties = PopupProperties(focusable = false),
                    onDismissRequest = { /*  */ },
                ) {
                    filteredTags.forEach { tag ->
                        InputChip(
                            modifier = Modifier.chipHeight(),
                            selected = false,
                            enabled = true,
                            onClick = {
                                onTagSelected(tag)
                                inputTextFieldState.clearText()
                                showDropdown = false
                            },
                            label = { Text(tag.label) },
                            colors = InputChipDefaults.inputChipColors(containerColor = tag.color),
                        )
                    }
                    if (inputText.isNotBlank()) {
                        NewTagChip(
                            text = inputText,
                            tagColor = randomNewTagColor,
                            onClick = {
                                onAddNewTagClicked(inputText)
                                inputTextFieldState.clearText()
                                showDropdown = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectedTagChip(
    tag: TagEntity,
    onRemoveClick: () -> Unit
) {
    InputChip(
        modifier = Modifier.chipHeight(),
        selected = true,
        enabled = false,
        onClick = {},
        label = { Text(tag.label) },
        colors = InputChipDefaults.inputChipColors(disabledContainerColor = tag.color),
        trailingIcon = {
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        onRemoveClick()
                    },
                imageVector = Icons.Filled.Close,
                contentDescription = null,
            )
        }
    )
}

@Composable
private fun NewTagChip(
    text: String,
    tagColor: Color,
    onClick: () -> Unit,
) {
    InputChip(
        modifier = Modifier.chipHeight(),
        selected = false,
        enabled = true,
        onClick = onClick,
        label = { Text(text) },
        colors = InputChipDefaults.inputChipColors(containerColor = tagColor),
        trailingIcon = {
            Icon(
                modifier = Modifier.clip(CircleShape),
                imageVector = Icons.Filled.Add,
                contentDescription = null,
            )
        }
    )
}

// By default, InputChip is padded to a 48dp min height based on accessibility guidelines,
// but its visual height is smaller. This bumps up visual height closer to accessibility minimum.
@Composable
private fun Modifier.chipHeight() =
    this
        .height(LocalMinimumInteractiveComponentSize.current - 6.dp)
        .padding(vertical = 3.dp)
