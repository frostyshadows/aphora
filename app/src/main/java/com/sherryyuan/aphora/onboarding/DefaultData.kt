package com.sherryyuan.aphora.onboarding

import com.sherryyuan.aphora.database.entities.QuoteEntity
import com.sherryyuan.aphora.database.entities.SourceCategory
import com.sherryyuan.aphora.database.entities.SourceEntity
import com.sherryyuan.aphora.database.entities.TagEntity
import com.sherryyuan.aphora.ui.theme.TagPastelBlue
import com.sherryyuan.aphora.ui.theme.TagPastelGreen
import com.sherryyuan.aphora.ui.theme.TagPastelOrange
import com.sherryyuan.aphora.ui.theme.TagPastelPink
import com.sherryyuan.aphora.ui.theme.TagPastelPurple
import com.sherryyuan.aphora.ui.theme.TagPastelRed
import com.sherryyuan.aphora.ui.theme.TagPastelYellow

data class DefaultQuoteBundle(
    val quote: QuoteEntity,
    val source: SourceEntity,
    val tags: List<String>,
)

val DEFAULT_TAGS = listOf(
    TagEntity(label = "character description", color = TagPastelRed),
    TagEntity(label = "setting", color = TagPastelOrange),
    TagEntity(label = "dialogue", color = TagPastelYellow),
    TagEntity(label = "action", color = TagPastelGreen),
    TagEntity(label = "interiority", color = TagPastelBlue),
    TagEntity(label = "exposition", color = TagPastelPurple),
    TagEntity(label = "world-building", color = TagPastelPink),
    TagEntity(label = "humor", color = TagPastelRed),
    TagEntity(label = "metaphor", color = TagPastelBlue),
)

val DEFAULT_QUOTE_BUNDLES = listOf(
    DefaultQuoteBundle(
        quote = QuoteEntity(
            text = "I'm responsible for my own happiness? I can't even be responsible for my own breakfast!",
            userNote = "BoJack talking to Diane in S1E1",
            rating = 5,
            timestampAdded = System.currentTimeMillis(),
            timestampLastEdited = System.currentTimeMillis(),
        ),
        source = SourceEntity(
            writer = "Raphael Bob-Waksberg",
            work = "BoJack Horseman",
            category = SourceCategory.TV,
        ),
        tags = listOf("dialogue", "humor"),
    ),
    DefaultQuoteBundle(
        quote = QuoteEntity(
            text = "One day I went with Sibylla to Tesco’s. A brilliant white light beat " +
                    "pitilessly down, like the fierce desert sun at midday on the French " +
                    "Foreign Legion; the glittering floor dazzled the eye with the cruel desert " +
                    "glare. We walked slowly through the cereals. Vast boxes of cornflakes and " +
                    "bran flakes rose on either side; as we reached the muesli a cart turned " +
                    "the corner and turned into the aisle, propelled by a fat woman and " +
                    "followed by three fat children. One was crying into a fat fist, and two " +
                    "were arguing about Frosties and Breakfast Boulders, and the woman was " +
                    "smiling.",
            userNote = "Of course Helen DeWitt can make Tesco interesting",
            rating = 4,
            timestampAdded = System.currentTimeMillis(),
            timestampLastEdited = System.currentTimeMillis(),
        ),
        source = SourceEntity(
            writer = "Helen Dewitt",
            work = "The Last Samurai",
            category = SourceCategory.BOOK,
        ),
        tags = listOf("setting"),
    ),
    DefaultQuoteBundle(
        quote = QuoteEntity(
            text = "What kind of magpie keeps this notebook?",
            userNote = "Didion's metaphor for collecting pretty shiny phrases in her notebook",
            rating = 3,
            timestampAdded = System.currentTimeMillis(),
            timestampLastEdited = System.currentTimeMillis(),
        ),
        source = SourceEntity(
            writer = "Joan Didion",
            work = "Slouching Towards Bethlehem",
            category = SourceCategory.BOOK,
        ),
        tags = listOf("metaphor"),
    )
)
