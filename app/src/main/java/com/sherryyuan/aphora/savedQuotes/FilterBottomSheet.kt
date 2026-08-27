package com.sherryyuan.aphora.savedQuotes

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sherryyuan.aphora.R
import com.sherryyuan.aphora.database.entities.SourceCategory
import com.sherryyuan.aphora.database.entities.TagEntity
import com.sherryyuan.aphora.ui.common.VerticalSpacer
import com.sherryyuan.aphora.ui.theme.LikeIconRed

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
                selectedMinRating = filterMinRating,
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
    selectedMinRating: Int,
    onRatingSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.wrapContentSize()) {
        for (i in 5 downTo 1) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    modifier = Modifier.size(32.dp),
                    selected = i == selectedMinRating,
                    onClick = { onRatingSelected(i) }
                )
                RatingHearts(i)
                Spacer(modifier = Modifier.width(12.dp))
                Text(stringResource(R.string.rating_and_up))
            }
        }
    }
}

@Composable
private fun RatingHearts(
    rating: Int,
    modifier: Modifier = Modifier,
) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        for (i in 1..5) {
            Image(
                painter = painterResource(R.drawable.icon_heart),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(
                    if (i <= rating) LikeIconRed else Color.Gray.copy(alpha = 0.3f)
                )
            )
        }
    }
}
