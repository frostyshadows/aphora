package com.sherryyuan.aphora.settings

import androidx.lifecycle.ViewModel
import com.sherryyuan.aphora.navigation.Navigator
import com.sherryyuan.aphora.navigation.SettingsExportKey
import com.sherryyuan.aphora.navigation.SettingsImportKey
import com.sherryyuan.aphora.navigation.SettingsSourcesKey
import com.sherryyuan.aphora.navigation.SettingsTagsKey
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val navigator: Navigator,
) : ViewModel() {

    fun navigateBack() {
        navigator.goBack()
    }

    fun viewSourcesSettings() {
        navigator.goTo(SettingsSourcesKey)
    }

    fun viewTagsSettings() {
        navigator.goTo(SettingsTagsKey)
    }

    fun importSnippets() {
        navigator.goTo(SettingsImportKey)
    }

    fun exportSnippets() {
        navigator.goTo(SettingsExportKey)
    }
}
