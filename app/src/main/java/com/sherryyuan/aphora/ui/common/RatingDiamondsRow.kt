package com.sherryyuan.aphora.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sherryyuan.aphora.R
import com.sherryyuan.aphora.ui.theme.DisabledBlue
import com.sherryyuan.aphora.ui.theme.GemstoneBlue

@Composable
fun RatingDiamondsRow(
    rating: Int,
    diamondSize: Dp,
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 12.dp,
    onRatingClick: ((Int) -> Unit)? = null,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
    ) {
        for (i in 1..5) {
            Image(
                modifier = Modifier
                    .size(diamondSize)
                    .then(
                        onRatingClick?.let {
                            Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(
                                    color = GemstoneBlue,
                                    bounded = false,
                                ),
                            ) {
                                onRatingClick(i)
                            }
                        } ?: Modifier
                    ),
                painter = painterResource(R.drawable.illo_diamond),
                colorFilter = if (i > rating) {
                    ColorFilter.tint(DisabledBlue)
                } else {
                    null
                },
                contentDescription = null,
            )
        }
    }
}