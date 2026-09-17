package com.sherryyuan.aphora.settings.sourcesSettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sherryyuan.aphora.navigation.Navigator
import com.sherryyuan.aphora.repository.QuotesRepository
import com.sherryyuan.aphora.repository.SourcesRepository
import com.sherryyuan.aphora.savedQuotes.SourceUiModel
import com.sherryyuan.aphora.savedQuotes.toUiModel
import com.sherryyuan.aphora.settings.sourcesSettings.SettingsSourcesViewState.SettingsSourcesModalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsSourcesViewModel @Inject constructor(
    private val navigator: Navigator,
    private val quotesRepository: QuotesRepository,
    private val sourcesRepository: SourcesRepository,
) : ViewModel() {

    private var sortOrderFlow: MutableStateFlow<SourceSortOrder> =
        MutableStateFlow(SourceSortOrder.MOST_REFERENCED)
    private val modalStateFlow: MutableStateFlow<SettingsSourcesViewState.SettingsSourcesModalState> =
        MutableStateFlow(SettingsSourcesViewState.SettingsSourcesModalState.None)

    val state: StateFlow<SettingsSourcesViewState> = createSettingsSourcesState()

    fun navigateBack() {
        navigator.goBack()
    }

    fun editClick(sourceId: Long) {
        val selectedSource = state.value.sourcesWithCount
            .map { it.first }
            .firstOrNull { it.existingId == sourceId }
        selectedSource?.let {
            viewModelScope.launch {
                val allSources = sourcesRepository.getAllSources().first()
                modalStateFlow.value = SettingsSourcesModalState.EditSource(
                    source = it,
                    allSources = allSources,
                )
            }
        }
    }

    fun saveSource(source: SourceUiModel) {
        viewModelScope.launch {
            sourcesRepository.updateSource(source)
        }
        modalStateFlow.value = SettingsSourcesModalState.None
    }

    // Delete directly if no sources are in use. Otherwise, show a confirmation dialogue.
    fun deleteClick(sourceIds: Set<Long>) {
        val areSourcesUsed = state.value.sourcesWithCount
            .any { (source, count) -> source.existingId in sourceIds && count > 0 }
        if (areSourcesUsed) {
            modalStateFlow.value = SettingsSourcesModalState.DeleteDialog
        } else {
            deleteSelectedSources(sourceIds)
        }
    }

    fun deleteSelectedSources(sourceIds: Set<Long>) {
        viewModelScope.launch {
            sourcesRepository.deleteSources(sourceIds.toList())
        }
        modalStateFlow.value = SettingsSourcesModalState.None
    }

    fun sortClick() {
        modalStateFlow.value = SettingsSourcesModalState.SortSheet(sortOrderFlow.value)
    }

    fun dismissModal() {
        modalStateFlow.value = SettingsSourcesModalState.None
    }

    fun selectSortOrder(sortOrder: SourceSortOrder) {
        sortOrderFlow.value = sortOrder
        modalStateFlow.value = SettingsSourcesModalState.None
    }

    private fun createSettingsSourcesState(): StateFlow<SettingsSourcesViewState> {
        return combine(
            sourcesRepository.getAllSources(),
            quotesRepository.getQuotes(),
            sortOrderFlow,
            modalStateFlow,
        ) { sources, quotes, sortOrder, modalState ->
            val sourcesCountMap = sources.associateWith { 0 }.toMutableMap()
            quotes.forEach { quote ->
                val source = quote.source ?: return@forEach
                val currentCount = sourcesCountMap[source]
                sourcesCountMap[source] = currentCount?.plus(1) ?: 1
            }
            val sourcesWithCount = when (sortOrder) {
                SourceSortOrder.MOST_REFERENCED -> sourcesCountMap.toList()
                    .sortedByDescending { it.second }

                SourceSortOrder.LEAST_REFERENCED -> sourcesCountMap.toList()
                    .sortedBy { it.second }
            }
            SettingsSourcesViewState(
                sourcesWithCount = sourcesWithCount.map { (sourceEntity, count) ->
                    sourceEntity.toUiModel() to count
                },
                modalState = modalState,
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            SettingsSourcesViewState(
                sourcesWithCount = emptyList(),
                modalState = SettingsSourcesModalState.None,
            ),
        )
    }
}
