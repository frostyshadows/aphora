package com.sherryyuan.aphora.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sherryyuan.aphora.R
import com.sherryyuan.aphora.ui.theme.LikeIconRed
import com.sherryyuan.aphora.ui.theme.Typography

@Composable
fun RatingHeart(rating: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Image(
            modifier = Modifier.size(32.dp),
            painter = painterResource(R.drawable.icon_heart),
            contentDescription = null,
            colorFilter = ColorFilter.tint(LikeIconRed),
        )
        Text(
            text = rating.toString(),
            style = Typography.labelLarge,
            color = Color.White,
        )
    }
}
