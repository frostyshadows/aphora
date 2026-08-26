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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
    onAddNewTagClicked: (String, Color) -> Unit,
    onTagUnselected: (TagEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    var randomNewTagColor = remember {
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
            text = stringResource(R.string.quote_tags_section_title)
        )

        FlowRow(
            modifier = modifier
                .border(
                    width = if (showDropdown) 2.dp else 1.dp,
                    color = if (showDropdown) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(4.dp)
                ),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            selectedTags.forEach { tag ->
                key(tag.tagId) {
                    SelectedTagChip(
                        modifier = Modifier.chipHeight(),
                        tag = tag,
                        onRemoveClick = { onTagUnselected(tag) },
                    )
                }
            }

            Box(
                modifier = Modifier
                    .height(54.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    state = inputTextFieldState,
                    modifier = Modifier
                        .fillMaxWidth()
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
                    modifier = Modifier.wrapContentWidth(),
                    containerColor = MaterialTheme.colorScheme.surface,
                    expanded = showDropdown,
                    properties = PopupProperties(focusable = false),
                    onDismissRequest = { /*  */ },
                ) {
                    filteredTags.forEach { tag ->
                        InputChip(
                            modifier = Modifier
                                .chipHeight()
                                .padding(horizontal = 8.dp),
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
                            modifier = Modifier
                                .chipHeight()
                                .padding(horizontal = 8.dp),
                            text = inputText,
                            tagColor = randomNewTagColor,
                            onClick = {
                                onAddNewTagClicked(inputText, randomNewTagColor)
                                randomNewTagColor =
                                    DefaultTagColors[DefaultTagColors.indices.random()]
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
    modifier: Modifier = Modifier,
    tag: TagEntity,
    onRemoveClick: () -> Unit
) {
    InputChip(
        modifier = modifier,
        selected = true,
        enabled = false,
        onClick = {},
        label = { Text(text = tag.label) },
        colors = InputChipDefaults.inputChipColors(
            labelColor = MaterialTheme.colorScheme.onSurface,
            trailingIconColor = MaterialTheme.colorScheme.onSurface,
            disabledSelectedContainerColor = tag.color,
        ),
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
    modifier: Modifier = Modifier,
    text: String,
    tagColor: Color,
    onClick: () -> Unit,
) {
    InputChip(
        modifier = modifier.chipHeight(),
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
