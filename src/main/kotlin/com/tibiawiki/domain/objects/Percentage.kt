package com.tibiawiki.domain.objects

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

class Percentage private constructor(
    @get:JsonValue val originalValue: String? = null,
    val value: Int? = null
) {
    companion object {
        @JvmStatic
        @JsonCreator
        fun of(value: String?): Percentage {
            return Percentage(value, sanitize(value))
        }

        @JvmStatic
        fun of(value: Int): Percentage {
            return Percentage("$value%", value)
        }

        private fun sanitize(value: String?): Int? {
            val sanitizedValue = value?.replace("\\D+".toRegex(), "").orEmpty()
            return if (sanitizedValue.isEmpty()) {
                null
            } else {
                sanitizedValue.toInt()
            }
        }
    }
}
