package com.tibiawiki.domain.enums

import com.fasterxml.jackson.annotation.JsonValue
import com.tibiawiki.domain.interfaces.Description

enum class Vocation(
    @get:JsonValue override val description: String
) : Description {
    KNIGHT("knight"),
    PALADIN("paladin"),
    DRUID("druid"),
    SORCERER("sorcerer")
}
