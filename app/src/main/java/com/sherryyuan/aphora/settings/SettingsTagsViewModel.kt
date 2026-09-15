package com.sherryyuan.aphora.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sherryyuan.aphora.navigation.Navigator
import com.sherryyuan.aphora.repository.QuotesRepository
import com.sherryyuan.aphora.repository.TagsRepository
import com.sherryyuan.aphora.settings.SettingsTagsViewState.SettingsTagsModalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsTagsViewModel @Inject constructor(
    private val navigator: Navigator,
    private val quotesRepository: QuotesRepository,
    private val tagsRepository: TagsRepository,
) : ViewModel() {

    private var sortOrderFlow: MutableStateFlow<TagSortOrder> =
        MutableStateFlow(TagSortOrder.MOST_TAGGED)
    private val modalStateFlow: MutableStateFlow<SettingsTagsModalState> =
        MutableStateFlow(SettingsTagsModalState.None)

    val state: StateFlow<SettingsTagsViewState> = createSettingsTagsState()

    fun navigateBack() {
        navigator.goBack()
    }

    // Delete directly if no tags are in use. Otherwise, show a confirmation dialogue.
    fun deleteClick(tagLabels: Set<String>) {
        val areTagsUsed = state.value.tagsWithCount
            .any { (tag, count) -> tag.label in tagLabels && count > 0 }
        if (areTagsUsed) {
            modalStateFlow.value = SettingsTagsModalState.DeleteDialog
        } else {
            deleteSelectedTags(tagLabels)
        }
    }

    fun deleteSelectedTags(tagLabels: Set<String>) {
        viewModelScope.launch {
            tagsRepository.deleteTags(tagLabels.toList())
        }
        modalStateFlow.value = SettingsTagsModalState.None
    }

    fun sortClick() {
        modalStateFlow.value = SettingsTagsModalState.SortSheet(sortOrderFlow.value)
    }

    fun dismissModal() {
        modalStateFlow.value = SettingsTagsModalState.None
    }

    fun selectSortOrder(sortOrder: TagSortOrder) {
        sortOrderFlow.value = sortOrder
        modalStateFlow.value = SettingsTagsModalState.None
    }

    private fun createSettingsTagsState(): StateFlow<SettingsTagsViewState> {
        return combine(
            tagsRepository.getTags(),
            quotesRepository.getQuotes(),
            sortOrderFlow,
            modalStateFlow,
        ) { tags, quotes, sortOrder, modalState ->
            val tagsCountMap = tags.associateWith { 0 }.toMutableMap()
            quotes.forEach { quote ->
                quote.tags.forEach { tag ->
                    val currentCount = tagsCountMap[tag]
                    tagsCountMap[tag] = currentCount?.plus(1) ?: 1
                }
            }
            val tagsWithCount = when (sortOrder) {
                TagSortOrder.MOST_TAGGED -> tagsCountMap.toList()
                    .sortedByDescending { it.second }

                TagSortOrder.LEAST_TAGGED -> tagsCountMap.toList()
                    .sortedBy { it.second }
            }
            SettingsTagsViewState(
                tagsWithCount = tagsWithCount,
                modalState = modalState,
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            SettingsTagsViewState(
                tagsWithCount = emptyList(),
                modalState = SettingsTagsModalState.None,
            ),
        )
    }
}
