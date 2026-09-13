package com.sherryyuan.aphora.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.sherryyuan.aphora.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContainer(
    viewModel: SettingsViewModel = hiltViewModel<SettingsViewModel>(),
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(R.drawable.icon_arrow_left),
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = stringResource(R.string.label_back),
                        )
                    }
                },
                title = {
                    Text(stringResource(R.string.label_settings))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                )
            )
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .consumeWindowInsets(contentPadding)
        ) {
            SettingsRow(
                labelRes = R.string.label_sources,
                onClick = { viewModel.viewSourcesSettings() }
            )
            SettingsDivider()
            SettingsRow(
                labelRes = R.string.label_tags,
                onClick = { viewModel.viewTagsSettings() }
            )
            // TODO: Enable import/export once feature are fully tested
//            SettingsDivider()
//            SettingsRow(
//                labelRes = R.string.label_import,
//                onClick = { viewModel.importSnippets() }
//            )
//            SettingsDivider()
//            SettingsRow(
//                labelRes = R.string.label_export,
//                onClick = { viewModel.exportSnippets() }
//            )
        }
    }
}
