# Quote sets

Importable quote collections, one JSON file per set.

| File | Set | Quotes | Source |
|---|---|---:|---|
| `opening_lines.json` | Opening Lines | 50 | Project Gutenberg |
| `beautiful_prose.json` | Beautiful Prose | 50 | Project Gutenberg |
| `writing_craft.json` | Writing Craft | 20 | Project Gutenberg |
| `film_and_television.json` | Film & Television | 20 | Wikiquote |
| `famous_figures.json` | Famous Figures | 100 | Wikiquote |

## Schema

```jsonc
{
  "id": "opening_lines",          // stable set id, matches the filename
  "title": "Opening Lines",
  "description": "…",
  "quoteCount": 50,               // == quotes.length
  "tags": ["exposition", "humor"],// every tag used anywhere in this set
  "attribution": { "source": "…", "license": "…", "licenseUrl": "…", "note": "…" },
  "quotes": [
    {
      "text": "It is a truth universally acknowledged, …",  // -> QuoteEntity.text
      "note": "…",                                          // -> QuoteEntity.userNote (see below)
      "writer": "Jane Austen",                              // -> SourceEntity.writer
      "work": "Pride and Prejudice",                        // -> SourceEntity.work
      "category": "BOOK",                                   // -> SourceEntity.category
      "tags": ["exposition", "humor"],                      // -> TagEntity.label
      "year": 1813,                                         // provenance only
      "gutenbergId": 1342                                   // provenance only
    }
  ]
}
```

`category` is always one of the `SourceCategory` enum names. `QuoteEntity.rating` has no
counterpart here — imported quotes are unrated.

**`note` is optional.**

**No writer appears more than twice in any one set** (in the 20-quote sets, no writer repeats at all). Enforced by the verifier. For
`film_and_television` the `writer` field is the full screenwriting credit, so two films
by the same person under different co-writer credits ("Billy Wilder and Charles
Brackett" vs "Billy Wilder and I. A. L. Diamond") count as separate writers.

Fields the app does not currently model, kept for provenance and safe to ignore:
`year`, `gutenbergId`, `sourceNote` (Wikiquote's own citation), `speaker` and `episode`
(film/TV only).

## Tags

`opening_lines`, `beautiful_prose` and `film_and_television` stay inside the existing
`DEFAULT_TAGS` vocabulary. Two sets introduce new labels, so **the importer must
get-or-create tags rather than assuming they exist**:

- `writing_craft` adds: plot, reading, style, voice
- `famous_figures` adds: art, life, literature, nature, philosophy, science

Every quote carries at least one tag; the verifier fails the build otherwise.

## Provenance and verification

The three Gutenberg sets were not transcribed from memory. Each quote was extracted
verbatim from the source text named by its `gutenbergId` and then machine-verified: the
normalised quote must appear as an exact substring of that text, or it is dropped. This
caught several widely-circulated misattributions during the build, which were removed —
"To love another person is to see the face of God" (from the musical, not Hugo's novel),
"I am not afraid of storms…" (not in *Little Women*), "If you look the right way…" (not in
*The Secret Garden*), and "six impossible things before breakfast" (*Through the
Looking-Glass*, not *Alice in Wonderland*).

The two Wikiquote sets are drawn only from sourced sections — "Attributed",
"Misattributed" and "Disputed" sections are excluded by the parser. A quote counts as
sourced only when a `**` citation line *directly follows* the `*` line it belongs to.
An earlier version of the parser got this wrong: when a bullet was skipped (for being
too long), the next citation attached to the preceding quote instead, which could
promote an unsourced line to "sourced" under someone else's citation. That is how
"The happiness of your life depends upon the quality of your thoughts" — Marcus
Aurelius, routinely misattributed to Austen — briefly entered the Austen candidates.
It is excluded now.

## Licensing

**Gutenberg sets** are public domain in the United States. Public-domain status is
jurisdictional; the US cutoff (works published 1930 or earlier) is more permissive than
some other countries.

**Wikiquote sets** are CC BY-SA 4.0. This carries real obligations: attribution and
share-alike must be preserved wherever the text is redistributed, so the app should
surface the `attribution` block somewhere reachable — a credits screen, or the set's
detail view before import. The underlying film and television dialogue is itself still
in copyright; these are short quotations with attribution, which is the same basis
Wikiquote operates on, but it is a fair-use judgement rather than a grant.

No song lyrics are included in any set, deliberately — music publishers litigate
lyric reproduction aggressively and there is no comparable clean source.
