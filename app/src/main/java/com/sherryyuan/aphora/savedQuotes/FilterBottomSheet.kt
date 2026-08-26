package com.sherryyuan.aphora.savedQuotes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.sherryyuan.aphora.R
import com.sherryyuan.aphora.database.entities.SourceCategory
import com.sherryyuan.aphora.database.entities.TagEntity
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
        ratings: List<Int>,
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
    var filterRatings: List<Int> by remember {
        mutableStateOf(emptyList())
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
            Row(modifier = Modifier.fillMaxWidth()) {
                TextButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        filterAuthors = emptyList()
                        filterWorks = emptyList()
                        filterCategories = emptyList()
                        filterTags = emptyList()
                        filterRatings = emptyList()
                        onFiltersApply(
                            filterAuthors,
                            filterWorks,
                            filterTags,
                            filterCategories,
                            filterRatings
                        )
                    }
                ) {
                    Text(stringResource(R.string.label_clear))
                }
                TextButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onFiltersApply(
                            filterAuthors,
                            filterWorks,
                            filterTags,
                            filterCategories,
                            filterRatings
                        )
                    }
                ) {
                    Text(stringResource(R.string.label_apply))
                }
            }
        }
    }
}
