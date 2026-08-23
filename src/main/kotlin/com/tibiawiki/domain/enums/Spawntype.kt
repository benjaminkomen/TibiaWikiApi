package com.tibiawiki.domain.enums

import com.fasterxml.jackson.annotation.JsonValue
import com.tibiawiki.domain.interfaces.Description

enum class Spawntype(
    @get:JsonValue override val description: String
) : Description {
    REGULAR("Regular"),
    RAID("Raid"),
    EVENT("Event"),
    UNIQUE("Unique"),
    TRIGGERED("Triggered"),
    UNBLOCKABLE("Unblockable"),
    EMPTY("")
}
