package com.sherryyuan.aphora.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sherryyuan.aphora.R

@Composable
fun SettingsRow(
    @StringRes labelRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(labelRes),
            style = MaterialTheme.typography.titleSmall,
        )
        Icon(
            modifier = Modifier.size(16.dp),
            painter = painterResource(R.drawable.icon_caret_right),
            tint = MaterialTheme.colorScheme.onBackground,
            contentDescription = null,
        )
    }
}

@Composable
fun SettingsDivider() {
    HorizontalDivider(color = MaterialTheme.colorScheme.background)
}
