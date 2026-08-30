package com.sherryyuan.aphora.savedQuotes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sherryyuan.aphora.R
import com.sherryyuan.aphora.database.entities.SourceCategory
import com.sherryyuan.aphora.database.entities.SourceEntity
import com.sherryyuan.aphora.database.entities.TagEntity
import com.sherryyuan.aphora.ui.common.RatingDiamondsRow
import com.sherryyuan.aphora.ui.common.ScrollableSelectionList
import com.sherryyuan.aphora.ui.common.VerticalSpacer
import com.sherryyuan.aphora.ui.theme.NavyAccent
import com.sherryyuan.aphora.ui.theme.Spacing

private enum class ExpandedSection {
    CATEGORIES,
    WRITER,
    WORKS,
    TAGS,
    RATING,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    selectedCategories: List<SourceCategory>,
    selectedWriters: List<String>,
    selectedWorks: List<String>,
    allSources: List<SourceEntity>,
    selectedTags: List<TagEntity>,
    allTags: List<TagEntity>,
    selectedMinRating: Int,
    onDismiss: () -> Unit,
    onFiltersApply: (
        writers: List<String>,
        works: List<String>,
        tags: List<TagEntity>,
        categories: List<SourceCategory>,
        minRating: Int,
    ) -> Unit,
    modifier: Modifier = Modifier,
) {
    var filterWriters: List<String> by remember(selectedWriters) {
        mutableStateOf(selectedWriters)
    }
    var filterWorks: List<String> by remember(selectedWorks) {
        mutableStateOf(selectedWorks)
    }
    var filterCategories: List<SourceCategory> by remember(selectedCategories) {
        mutableStateOf(selectedCategories)
    }
    var filterTags: List<TagEntity> by remember(selectedTags) {
        mutableStateOf(selectedTags)
    }
    var expandedSection: ExpandedSection? by remember {
        mutableStateOf(null)
    }
    var filterMinRating: Int by remember(selectedMinRating) {
        mutableIntStateOf(selectedMinRating)
    }

    val availableWriters = remember(allSources, filterCategories) {
        allSources
            .filter { filterCategories.isEmpty() || it.category in filterCategories }
            .mapNotNull { it.writer }
            .distinct()
    }
    LaunchedEffect(availableWriters) {
        filterWriters = filterWriters.filter { it in availableWriters }
    }

    val effectiveWriters = filterWriters.ifEmpty { availableWriters }
    val availableWorks = remember(allSources, effectiveWriters) {
        allSources
            .filter { it.writer in effectiveWriters && it.work != null }
            .distinctBy { it.work }
    }
    LaunchedEffect(availableWorks) {
        val availableWorkTitles = availableWorks.map { it.work }
        filterWorks = filterWorks.filter { it in availableWorkTitles }
    }

    ModalBottomSheet(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        onDismissRequest = onDismiss,
    ) {
        Column {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.label_filter),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )
            VerticalSpacer()
            CategoriesFilter(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.ScreenMargin),
                isExpanded = expandedSection == ExpandedSection.CATEGORIES,
                onHeaderClick = {
                    expandedSection = if (expandedSection == ExpandedSection.CATEGORIES) {
                        null
                    } else {
                        ExpandedSection.CATEGORIES
                    }
                },
                selectedCategories = filterCategories,
                updateCategories = { filterCategories = it },
            )
            VerticalSpacer()
            WritersFilter(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.ScreenMargin),
                isExpanded = expandedSection == ExpandedSection.WRITER,
                onHeaderClick = {
                    expandedSection = if (expandedSection == ExpandedSection.WRITER) {
                        null
                    } else {
                        ExpandedSection.WRITER
                    }
                },
                availableWriters = availableWriters,
                selectedWriters = filterWriters,
                onWriterToggled = { writer ->
                    filterWriters = if (writer in filterWriters) {
                        filterWriters - writer
                    } else {
                        filterWriters + writer
                    }
                },
            )
            VerticalSpacer()
            WorksFilter(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.ScreenMargin),
                isExpanded = expandedSection == ExpandedSection.WORKS,
                onHeaderClick = {
                    expandedSection = if (expandedSection == ExpandedSection.WORKS) {
                        null
                    } else {
                        ExpandedSection.WORKS
                    }
                },
                availableWorkSources = availableWorks,
                includeWriterSuffix = effectiveWriters.size > 1,
                selectedWorks = filterWorks,
                onWorkToggled = { work ->
                    filterWorks = if (work in filterWorks) {
                        filterWorks - work
                    } else {
                        filterWorks + work
                    }
                },
            )
            VerticalSpacer()
            TagsFilter(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.ScreenMargin),
                isExpanded = expandedSection == ExpandedSection.TAGS,
                onHeaderClick = {
                    expandedSection = if (expandedSection == ExpandedSection.TAGS) {
                        null
                    } else {
                        ExpandedSection.TAGS
                    }
                },
                availableTags = allTags,
                selectedTags = filterTags,
                onTagToggled = { tag ->
                    filterTags = if (tag in filterTags) {
                        filterTags - tag
                    } else {
                        filterTags + tag
                    }
                },
            )
            VerticalSpacer()
            RatingsFilter(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.ScreenMargin),
                isExpanded = expandedSection == ExpandedSection.RATING,
                selectedMinRating = filterMinRating,
                onHeaderClick = {
                    expandedSection = if (expandedSection == ExpandedSection.RATING) {
                        null
                    } else {
                        ExpandedSection.RATING
                    }
                },
                updateRating = { filterMinRating = it },
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.ScreenMargin)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        filterWriters = emptyList()
                        filterWorks = emptyList()
                        filterCategories = emptyList()
                        filterTags = emptyList()
                        filterMinRating = 1
                        onFiltersApply(
                            filterWriters,
                            filterWorks,
                            filterTags,
                            filterCategories,
                            filterMinRating,
                        )
                    }
                ) {
                    Text(stringResource(R.string.label_clear))
                }
                Spacer(modifier = Modifier.width(20.dp))
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onFiltersApply(
                            filterWriters,
                            filterWorks,
                            filterTags,
                            filterCategories,
                            filterMinRating,
                        )
                    }
                ) {
                    Text(stringResource(R.string.label_apply))
                }
            }
        }
    }
}

@Composable
private fun CategoriesFilter(
    isExpanded: Boolean,
    onHeaderClick: () -> Unit,
    selectedCategories: List<SourceCategory>,
    updateCategories: (List<SourceCategory>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isAllSelected = selectedCategories.size == SourceCategory.entries.size
    Column(modifier) {
        FilterSectionHeader(
            title = filterHeaderText(
                stringResource(R.string.categories_header),
                selectedCategories.size
            ),
            isExpanded = isExpanded,
            onClick = onHeaderClick,
        )
        AnimatedVisibility(visible = isExpanded) {
            Column {
                VerticalSpacer(8.dp)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    CategoryInputChip(
                        label = stringResource(R.string.category_all),
                        selected = isAllSelected,
                        onClick = {
                            if (isAllSelected) {
                                updateCategories(emptyList())
                            } else {
                                updateCategories(SourceCategory.entries)
                            }
                        },
                    )
                    SourceCategory.entries.forEach { category ->
                        val isSelected = category in selectedCategories
                        CategoryInputChip(
                            label = stringResource(category.stringRes),
                            selected = isSelected,
                            onClick = {
                                if (isSelected) {
                                    updateCategories(selectedCategories - category)
                                } else {
                                    updateCategories(selectedCategories + category)
                                }
                            },
                        )
                    }
                }
                VerticalSpacer(8.dp)
            }
        }
    }
}

@Composable
private fun CategoryInputChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    InputChip(
        modifier = modifier,
        selected = selected,
        enabled = true,
        onClick = onClick,
        label = { Text(label) },
        colors = InputChipDefaults.inputChipColors(
            containerColor = Color.Transparent,
            labelColor = MaterialTheme.colorScheme.onSurface,
            selectedContainerColor = NavyAccent,
            selectedLabelColor = Color.White,
        ),
        border = InputChipDefaults.inputChipBorder(
            enabled = true,
            selected = selected,
            borderColor = MaterialTheme.colorScheme.onSurface,
        ),
    )
}

@Composable
private fun WritersFilter(
    isExpanded: Boolean,
    onHeaderClick: () -> Unit,
    availableWriters: List<String>,
    selectedWriters: List<String>,
    onWriterToggled: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by remember { mutableStateOf("") }
    Column(modifier) {
        FilterSectionHeader(
            title = filterHeaderText(stringResource(R.string.filter_header_writers), selectedWriters.size),
            isExpanded = isExpanded,
            onClick = onHeaderClick,
        )
        AnimatedVisibility(visible = isExpanded) {
            Column {
                VerticalSpacer(8.dp)
                FilterSearchField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = stringResource(R.string.search_writers_placeholder),
                )
                VerticalSpacer(8.dp)
                val displayedWriters = remember(availableWriters, selectedWriters, searchQuery) {
                    availableWriters
                        .filter { it.contains(searchQuery, ignoreCase = true) }
                        .sortedWith(compareBy({ it !in selectedWriters }, { it.lowercase() }))
                }
                ScrollableSelectionList(items = displayedWriters) { writer ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onWriterToggled(writer) },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(modifier = Modifier.weight(1f), text = writer)
                        Checkbox(
                            modifier = Modifier.size(32.dp),
                            checked = writer in selectedWriters,
                            onCheckedChange = { onWriterToggled(writer) })
                    }
                }
                VerticalSpacer(8.dp)
            }
        }
    }
}

@Composable
private fun WorksFilter(
    isExpanded: Boolean,
    onHeaderClick: () -> Unit,
    availableWorkSources: List<SourceEntity>,
    includeWriterSuffix: Boolean,
    selectedWorks: List<String>,
    onWorkToggled: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by remember { mutableStateOf("") }
    Column(modifier) {
        FilterSectionHeader(
            title = filterHeaderText(stringResource(R.string.filter_header_works), selectedWorks.size),
            isExpanded = isExpanded,
            onClick = onHeaderClick,
        )
        AnimatedVisibility(visible = isExpanded) {
            Column {
                VerticalSpacer(8.dp)
                FilterSearchField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = stringResource(R.string.search_works_placeholder),
                )
                VerticalSpacer(8.dp)
                val displayedWorks = remember(availableWorkSources, selectedWorks, searchQuery) {
                    availableWorkSources
                        .filter { it.work?.contains(searchQuery, ignoreCase = true) == true }
                        .sortedWith(
                            compareBy(
                                { it.work !in selectedWorks },
                                { it.work?.lowercase() })
                        )
                }
                ScrollableSelectionList(items = displayedWorks) { option ->
                    val label = if (includeWriterSuffix) {
                        val lastNameOrSelf = option.writer?.trim()?.substringAfterLast(' ')
                        "${option.work} ($lastNameOrSelf)"
                    } else {
                        option.work
                    }
                    label?.let { label ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    option.work?.let { onWorkToggled(it) }
                                },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(modifier = Modifier.weight(1f), text = label)
                            Checkbox(
                                modifier = Modifier.size(32.dp),
                                checked = option.work in selectedWorks,
                                onCheckedChange = {
                                    option.work?.let { onWorkToggled(it) }
                                }
                            )
                        }
                    }
                }
                VerticalSpacer(8.dp)
            }
        }
    }
}

@Composable
private fun TagsFilter(
    isExpanded: Boolean,
    onHeaderClick: () -> Unit,
    availableTags: List<TagEntity>,
    selectedTags: List<TagEntity>,
    onTagToggled: (TagEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by remember { mutableStateOf("") }
    Column(modifier) {
        FilterSectionHeader(
            title = filterHeaderText(
                stringResource(R.string.quote_tags_section_title),
                selectedTags.size,
            ),
            isExpanded = isExpanded,
            onClick = onHeaderClick,
        )
        AnimatedVisibility(visible = isExpanded) {
            Column {
                VerticalSpacer(8.dp)
                FilterSearchField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = stringResource(R.string.search_tags_placeholder),
                )
                VerticalSpacer(8.dp)
                val displayedTags = remember(availableTags, selectedTags, searchQuery) {
                    availableTags
                        .filter { it.label.contains(searchQuery, ignoreCase = true) }
                        .sortedWith(compareBy({ it !in selectedTags }, { it.label.lowercase() }))
                }
                ScrollableSelectionList(items = displayedTags) { tag ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTagToggled(tag) },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(modifier = Modifier.weight(1f), text = tag.label)
                        Checkbox(
                            modifier = Modifier.size(32.dp),
                            checked = tag in selectedTags,
                            onCheckedChange = { onTagToggled(tag) },
                        )
                    }
                }
                VerticalSpacer(8.dp)
            }
        }
    }
}

@Composable
private fun RatingsFilter(
    isExpanded: Boolean,
    selectedMinRating: Int,
    onHeaderClick: () -> Unit,
    updateRating: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Crossfade(targetState = isExpanded) { expanded ->
            if (expanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onHeaderClick() }
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.filter_header_rating),
                    )
                    Icon(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(R.drawable.icon_caret_up),
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = null,
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onHeaderClick() }
                ) {
                    Text(stringResource(R.string.filter_header_rating))
                    Spacer(modifier = Modifier.width(8.dp))
                    RatingDiamondsRow(
                        rating = selectedMinRating,
                        diamondSize = 20.dp,
                        horizontalSpacing = 4.dp,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.rating_and_up),
                    )
                    Icon(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(R.drawable.icon_caret_down),
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = null,
                    )
                }
            }
        }
        AnimatedVisibility(visible = isExpanded) {
            Column {
                VerticalSpacer(8.dp)
                for (i in 5 downTo 1) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            modifier = Modifier.size(32.dp),
                            selected = i == selectedMinRating,
                            onClick = { updateRating(i) }
                        )
                        RatingDiamondsRow(rating = i, diamondSize = 20.dp, horizontalSpacing = 4.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(stringResource(R.string.rating_and_up))
                    }
                }
            }
        }
    }
}

@Composable
private fun filterHeaderText(baseLabel: String, selectedCount: Int): String =
    if (selectedCount > 0) {
        stringResource(R.string.filter_header_with_count, baseLabel, selectedCount)
    } else {
        baseLabel
    }

@Composable
private fun FilterSectionHeader(
    title: String,
    isExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(modifier = Modifier.weight(1f), text = title)
        Icon(
            modifier = Modifier.size(16.dp),
            painter = painterResource(
                if (isExpanded) R.drawable.icon_caret_up else R.drawable.icon_caret_down
            ),
            tint = MaterialTheme.colorScheme.onBackground,
            contentDescription = null,
        )
    }
}

@Composable
private fun FilterSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        singleLine = true,
    )
}
