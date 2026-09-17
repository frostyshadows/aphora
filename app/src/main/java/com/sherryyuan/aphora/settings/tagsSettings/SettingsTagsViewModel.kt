package com.sherryyuan.aphora.settings.tagsSettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sherryyuan.aphora.navigation.Navigator
import com.sherryyuan.aphora.repository.TagsRepository
import com.sherryyuan.aphora.settings.tagsSettings.SettingsTagsViewState.SettingsTagsModalState
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
            // Make copy of selected set.
            modalStateFlow.value = SettingsTagsModalState.DeleteDialog(tagLabels.toSet())
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
            tagsRepository.getTagReferenceCounts(),
            sortOrderFlow,
            modalStateFlow,
        ) { tags, referenceCounts, sortOrder, modalState ->
            val tagsWithCount = tags.map { tag ->
                tag to (referenceCounts[tag.tagId] ?: 0)
            }
            SettingsTagsViewState(
                tagsWithCount = when (sortOrder) {
                    TagSortOrder.MOST_TAGGED -> tagsWithCount
                        .sortedByDescending { it.second }

                    TagSortOrder.LEAST_TAGGED -> tagsWithCount
                        .sortedBy { it.second }
                },
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
