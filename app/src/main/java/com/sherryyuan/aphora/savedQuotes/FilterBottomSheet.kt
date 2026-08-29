package com.sherryyuan.aphora.savedQuotes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sherryyuan.aphora.R
import com.sherryyuan.aphora.database.entities.SourceCategory
import com.sherryyuan.aphora.database.entities.TagEntity
import com.sherryyuan.aphora.ui.common.RatingDiamondsRow
import com.sherryyuan.aphora.ui.common.VerticalSpacer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    onDismiss: () -> Unit,
    onFiltersApply: (
        authors: List<String>,
        works: List<String>,
        tags: List<TagEntity>,
        categories: List<SourceCategory>,
        minRating: Int,
    ) -> Unit,
    modifier: Modifier = Modifier,
) {
    var filterAuthors: List<String> by remember {
        mutableStateOf(emptyList())
    }
    var filterWorks: List<String> by remember {
        mutableStateOf(emptyList())
    }
    var filterCategories: List<SourceCategory> by remember {
        mutableStateOf(emptyList())
    }
    var filterTags: List<TagEntity> by remember {
        mutableStateOf(emptyList())
    }
    var ratingFilterExpanded: Boolean by remember {
        mutableStateOf(false)
    }
    var filterMinRating: Int by remember {
        mutableIntStateOf(1)
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
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.label_author))
            }
            RatingsFilter(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                isExpanded = ratingFilterExpanded,
                selectedMinRating = filterMinRating,
                onCollapseToggle = { ratingFilterExpanded = !ratingFilterExpanded },
                onRatingSelected = { filterMinRating = it },
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        filterAuthors = emptyList()
                        filterWorks = emptyList()
                        filterCategories = emptyList()
                        filterTags = emptyList()
                        filterMinRating = 1
                        onFiltersApply(
                            filterAuthors,
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
                            filterAuthors,
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
private fun RatingsFilter(
    isExpanded: Boolean,
    selectedMinRating: Int,
    onCollapseToggle: () -> Unit,
    onRatingSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Crossfade(targetState = isExpanded) { expanded ->
            if (expanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCollapseToggle() }
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.header_rating_expanded),
                    )
                    Icon(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(R.drawable.icon_caret_down),
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = null,
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCollapseToggle() }
                ) {
                    Text(stringResource(R.string.header_rating_collapsed))
                    Spacer(modifier = Modifier.width(4.dp))
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
                        painter = painterResource(R.drawable.icon_caret_up),
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
                            onClick = { onRatingSelected(i) }
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
