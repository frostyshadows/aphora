package com.sherryyuan.aphora.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sherryyuan.aphora.R
import com.sherryyuan.aphora.ui.theme.GemstoneBlue
import com.sherryyuan.aphora.ui.theme.NavyAccent
import com.sherryyuan.aphora.ui.theme.Typography

@Composable
fun RatingDiamondSingle(rating: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Image(
            modifier = Modifier.size(32.dp),
            painter = painterResource(R.drawable.img_diamond),
            contentDescription = null,
            colorFilter = ColorFilter.tint(GemstoneBlue),
        )
        Text(
            modifier = Modifier.padding(bottom = 4.dp),
            text = rating.toString(),
            style = Typography.labelLarge,
            color = NavyAccent,
        )
    }
}
