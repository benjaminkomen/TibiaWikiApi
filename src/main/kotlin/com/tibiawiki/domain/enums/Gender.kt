package com.tibiawiki.domain.enums

import com.fasterxml.jackson.annotation.JsonValue
import com.tibiawiki.domain.interfaces.Description

enum class Gender(
    @get:JsonValue override val description: String
) : Description {
    FEMALE("Female"),
    MALE("Male"),
    UNKNOWN("Unknown"),
    EMPTY("")
}
