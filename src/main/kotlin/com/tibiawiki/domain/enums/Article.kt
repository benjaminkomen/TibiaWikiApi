package com.tibiawiki.domain.enums

import com.fasterxml.jackson.annotation.JsonValue
import com.tibiawiki.domain.interfaces.Description

enum class Article(
    @get:JsonValue override val description: String
) : Description {
    A("a"),
    AN("an"),
    EMPTY("")
}
