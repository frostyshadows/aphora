package com.sherryyuan.aphora.settings

import androidx.lifecycle.ViewModel
import com.sherryyuan.aphora.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsTagsViewModel @Inject constructor(
    private val navigator: Navigator,
) : ViewModel() {

    fun navigateBack() {
        navigator.goBack()
    }
}
