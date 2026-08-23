package com.tibiawiki.domain.enums

import com.fasterxml.jackson.annotation.JsonValue

enum class Star(
    @get:JsonValue val number: Int
) {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5)
}
