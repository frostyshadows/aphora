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
)

val DEFAULT_QUOTE_BUNDLES = listOf(
    DefaultQuoteBundle(
        quote = QuoteEntity(
            text = "The front was broken by a line of French windows, glowing now with reflected gold, " +
                    "and wide open to the warm windy afternoon, and Tom Buchanan in riding clothes " +
                    "was standing with his legs apart on the front porch. He had changed since his " +
                    "New Haven years. Now he was a sturdy, straw haired man of thirty with a " +
                    "rather hard mouth and a supercilious manner. Two shining, arrogant eyes had " +
                    "established dominance over his face and gave him the appearance of always leaning " +
                    "aggressively forward.",
            userNote = null,
            rating = 1,
        ),
        source = SourceEntity(
            author = "F. Scott Fitzgerald",
            work = "The Great Gatsby",
            category = SourceCategory.BOOK,
        ),
        tags = listOf("character description"),
    )
)
