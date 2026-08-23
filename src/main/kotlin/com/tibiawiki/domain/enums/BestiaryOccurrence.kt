package com.tibiawiki.domain.enums

import com.fasterxml.jackson.annotation.JsonValue
import com.tibiawiki.domain.interfaces.Description

enum class BestiaryOccurrence(
    @get:JsonValue override val description: String
) : Description {
    COMMON("Common"),
    UNCOMMON("Uncommon"),
    RARE("Rare"),
    VERY_RARE("Very Rare")
}
