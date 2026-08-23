package com.tibiawiki.domain.enums

import com.fasterxml.jackson.annotation.JsonValue
import com.tibiawiki.domain.interfaces.Description

enum class QuestType(
    @get:JsonValue override val description: String
) : Description {
    WORLD_CHANGE("change"),
    MINI_WORLD_CHANGE("mwc"),
    WORLD_EVENT("event"),
    WORLD_TASK("task")
}
