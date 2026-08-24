package com.tibiawiki.domain.enums

import com.fasterxml.jackson.annotation.JsonValue
import com.tibiawiki.domain.interfaces.Description

enum class Vocation(
    @get:JsonValue override val description: String
) : Description {
    KNIGHT("knight"),
    PALADIN("paladin"),
    DRUID("druid"),
    SORCERER("sorcerer"),
    MONK("monk");

    companion object {
        private val ALL_VOCATIONS = Regex("""^\s*all\s+vocations?\.?\s*$""", RegexOption.IGNORE_CASE)
        private val WIKI_LINK = Regex("""\[\[([^|\]]+)(?:\|[^\]]*)?]]""")
        private val TOKENS: List<Pair<Regex, Vocation>> = listOf(
            Regex("""\b(?:elite\s+)?knights?\b""", RegexOption.IGNORE_CASE) to KNIGHT,
            Regex("""\b(?:royal\s+)?paladins?\b""", RegexOption.IGNORE_CASE) to PALADIN,
            Regex("""\b(?:elder\s+)?druids?\b""", RegexOption.IGNORE_CASE) to DRUID,
            Regex("""\b(?:master\s+)?sorcerers?\b""", RegexOption.IGNORE_CASE) to SORCERER,
            Regex("""\b(?:exalted\s+)?monks?\b""", RegexOption.IGNORE_CASE) to MONK
        )

        /**
         * Parses Infobox Spell `voc` wiki text into typed vocations, including Monk.
         * Examples: `[[Monk]]s`, `[[Paladin]]s, [[Druid]]s and [[Sorcerer]]s`.
         */
        @JvmStatic
        fun parseVoc(raw: String?): List<Vocation> {
            if (raw.isNullOrBlank()) {
                return emptyList()
            }
            val text = raw.trim()
            if (ALL_VOCATIONS.matches(text)) {
                return entries.toList()
            }

            val links = WIKI_LINK.findAll(text).map { it.groupValues[1] }.toList()
            val searchIn = if (links.isNotEmpty()) links.joinToString(" ") else text
            val hits = TOKENS.flatMap { (regex, vocation) ->
                regex.findAll(searchIn).map { it.range.first to vocation }
            }
            return hits.sortedBy { it.first }.map { it.second }.distinct()
        }
    }
}
