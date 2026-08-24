package com.tibiawiki.domain.enums

import com.fasterxml.jackson.annotation.JsonValue
import com.tibiawiki.domain.interfaces.Description

enum class Status(
    @get:JsonValue override val description: String
) : Description {
    ACTIVE("Active"),
    ACTIVE_LOWERCASE("active"),
    DEPRECATED("deprecated"),
    UNOBTAINABLE("unobtainable"),
    UNAVAILABLE("unavailable"),
    TS_ONLY_LOWERCASE("ts-only"),
    TS_ONLY_UPPERCASE("TS-only"),
    EVENT("event"),
    EMPTY("")
}
