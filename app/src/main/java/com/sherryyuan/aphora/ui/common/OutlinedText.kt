package com.sherryyuan.aphora.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun OutlinedText(
    text: String,
    outlineColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // Outline layers
        Text(text, color = outlineColor, modifier = Modifier.offset(1.dp, 1.dp))
        Text(text, color = outlineColor, modifier = Modifier.offset((-1).dp, 1.dp))
        Text(text, color = outlineColor, modifier = Modifier.offset(1.dp, (-1).dp))
        Text(text, color = outlineColor, modifier = Modifier.offset((-1).dp, (-1).dp))

        // Main text
        Text(text, color = textColor)
    }
}
