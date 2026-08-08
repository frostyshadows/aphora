package com.sherryyuan.aphora.savedQuotes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sherryyuan.aphora.R
import com.sherryyuan.aphora.database.entities.TagEntity
import com.sherryyuan.aphora.mockData.createQuoteViewModel
import com.sherryyuan.aphora.ui.common.AphoraCard
import com.sherryyuan.aphora.ui.common.VerticalSpacer
import com.sherryyuan.aphora.ui.theme.AphoraTheme
import com.sherryyuan.aphora.ui.theme.LikeIconRed
import com.sherryyuan.aphora.ui.theme.Typography

@Composable
fun QuoteDetailCard(
    model: QuoteUiModel,
    onEditQuoteClick: () -> Unit,
    onDeleteQuoteClick: () -> Unit, // TODO: Show confirmation modal first
    onShareQuoteClick: () -> Unit,
) {
    AphoraCard(showBorder = true) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            VerticalSpacer()
            Image(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .size(32.dp),
                painter = painterResource(R.drawable.image_quote_start),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
            VerticalSpacer(8.dp)
            Text(
                modifier = Modifier.padding(horizontal = 20.dp),
                text = model.text,
                style = Typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )

            model.source?.let {
                VerticalSpacer()
                HorizontalDivider(
                    modifier = Modifier
                        .width(80.dp)
                        .align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                )
                VerticalSpacer(16.dp)
                QuoteSource(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 20.dp),
                    model = it,
                )
            }
            if (model.tags.isNotEmpty()) {
                VerticalSpacer(32.dp)
                QuoteTags(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    tags = model.tags,
                )
            }
            VerticalSpacer()
            RatingHeart(
                modifier = Modifier
                    .align(alignment = Alignment.End)
                    .padding(horizontal = 20.dp),
                rating = model.rating,
            )
            VerticalSpacer()
            ActionsRow(onEditQuoteClick, onDeleteQuoteClick, onShareQuoteClick)
        }
    }
}

@Composable
private fun QuoteSource(model: QuoteUiModel.Source, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(end = 24.dp)) {
        Text(
            text = model.author.uppercase(),
            style = Typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        model.work?.let { work ->
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                model.category?.let { category ->
                    Icon(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(16.dp),
                        painter = painterResource(category.iconRes),
                        contentDescription = stringResource(category.stringRes),
                    )
                }
                Text(
                    text = work,
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
    }
}

@Composable
private fun QuoteTags(tags: List<TagEntity>, modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        tags.forEach { tag ->
            key(tag.tagId) {
                Text(
                    modifier = Modifier
                        .background(color = tag.color, shape = RoundedCornerShape(50))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    text = tag.label,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = Typography.labelMedium,
                )
            }
        }
    }
}

@Composable
private fun RatingHeart(rating: Int, modifier: Modifier = Modifier) {
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

@Composable
private fun ActionsRow(
    onEditQuoteClick: () -> Unit,
    onDeleteQuoteClick: () -> Unit,
    onShareQuoteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        IconButton(onClick = onEditQuoteClick) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.icon_pencil),
                tint = MaterialTheme.colorScheme.background,
                contentDescription = stringResource(R.string.label_edit),
            )
        }
        IconButton(onClick = onDeleteQuoteClick) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.icon_delete),
                tint = MaterialTheme.colorScheme.background,
                contentDescription = stringResource(R.string.label_delete),
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onShareQuoteClick) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.icon_share),
                tint = MaterialTheme.colorScheme.background,
                contentDescription = stringResource(R.string.cd_share),
            )
        }
    }
}

@Preview
@Composable
fun QuoteDetailCardPreview() {
    AphoraTheme {
        Box(modifier = Modifier.fillMaxWidth()) {
            QuoteDetailCard(
                model = createQuoteViewModel(),
                onEditQuoteClick = {},
                onDeleteQuoteClick = {},
                onShareQuoteClick = {},
            )
        }
    }
}
